
package meci_e_services.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

public class ExternalConfigInitializer
        implements EnvironmentPostProcessor, Ordered {

    /** Répertoire externe de configuration. */
    private static final Path CONFIG_DIR = resolveConfigDirectory();

    /** Fichier externe de configuration */
    private static final Path CONFIG_FILE =
            CONFIG_DIR.resolve("application.yml");

    private static Path resolveConfigDirectory() {
        String configuredPath = System.getenv("MECI_CONFIG_DIR");
        if (configuredPath != null && !configuredPath.isBlank()) {
            return Paths.get(configuredPath).toAbsolutePath().normalize();
        }

        Path userHome = Paths.get(System.getProperty("user.home", ".")).toAbsolutePath().normalize();
        return userHome.resolve(".meci-e-services").resolve("config");
    }


    @Override
    public void postProcessEnvironment(
            ConfigurableEnvironment environment,
            SpringApplication application) {

        try {

            System.out.println(
                    "Initialisation de la configuration MECI..."
            );

            System.out.println(
                    "Fichier : " + CONFIG_FILE
            );


            // Création du dossier


            if (!Files.exists(CONFIG_DIR)) {

                Files.createDirectories(CONFIG_DIR);

                System.out.println(
                        "Dossier de configuration créé."
                );
            }


            // Création du fichier

            if (!Files.exists(CONFIG_FILE)) {

                createDefaultConfig();

                System.out.println(
                        "Fichier application.yml créé."
                );

                System.out.println(
                        "Vous pouvez maintenant modifier : "
                                + CONFIG_FILE
                );
            }

            // 3. Chargement de la configuration externe

            loadYamlConfiguration(environment);

            // 4. Création automatique de la base de données si elle n'existe pas
            autoCreateDatabase(environment);

            System.out.println(
                    "Configuration externe chargée avec succès."
            );


        } catch (Exception e) {

            throw new RuntimeException(
                    "Impossible d'initialiser la configuration externe : "
                            + CONFIG_FILE,
                    e
            );
        }
    }


    /**  Création du fichier de configuration par défaut */
    private void createDefaultConfig() throws IOException {

        String config = """

                # CONFIGURATION MECI E-SERVICES
                # Connexion PostgreSQL locale. Les variables MECI_DB_* peuvent
                # remplacer ces valeurs sans stocker de secret dans le code.

                jwt:
                  secret: dev-secret-key-for-local-testing-32-chars!
                  expiration: 86400000

                spring:

                  datasource:
                    url: ${MECI_DB_URL:jdbc:postgresql://localhost:5432/meci_eservices}
                    username: ${MECI_DB_USERNAME:postgres}
                    password: ${MECI_DB_PASSWORD:}
                    driver-class-name: org.postgresql.Driver

                    hikari:
                      connection-timeout: 30000
                      idle-timeout: 600000
                      max-lifetime: 1800000
                      minimum-idle: 1
                      maximum-pool-size: 10
                      validation-timeout: 3000

                  mail:
                    host: localhost
                    port: 2525
                    username: dev@meci.local
                    password: ""
                    properties:
                      mail:
                        smtp:
                          auth: false
                          starttls:
                            enable: false

                  jpa:
                    open-in-view: false
                    database-platform: org.hibernate.dialect.PostgreSQLDialect
                    show-sql: false
                    hibernate:
                      ddl-auto: update

                    properties:
                      hibernate:
                        format_sql: true
                        enable_lazy_load_no_trans: true
                        current_session_context_class: thread

                  output:
                    ansi:
                      enabled: ALWAYS

                server:
                  port: 8080
                """;

        Files.writeString(CONFIG_FILE, config);
    }


    /**
     * Chargement du fichier YAML externe
     */
    private void loadYamlConfiguration(
            ConfigurableEnvironment environment)
            throws IOException {

            Yaml yaml = new Yaml();
            try (InputStream inputStream = Files.newInputStream(CONFIG_FILE)) {
                Map<String, Object> yamlData = yaml.load(inputStream);

                if (yamlData == null) {
                    throw new IllegalStateException(
                            "Le fichier de configuration externe est vide : "
                                    + CONFIG_FILE
                    );
                }

                Properties properties = new Properties();
                flattenMap("", yamlData, properties);
                environment.getPropertySources().addFirst(
                        new PropertiesPropertySource(
                                "externalApplicationConfig",
                                properties
                        )
                );
            }
    }


    /**
     * Transforme le YAML en propriétés Spring Boot
     *
     * Exemple :
     *
     * spring:
     *   datasource:
     *     url: ...
     *
     * devient :
     *
     * spring.datasource.url
     */
    @SuppressWarnings("unchecked")
    private void flattenMap(
            String prefix,
            Map<String, Object> map,
            Properties properties) {

        if (map == null) {
            return;
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {

            String key = prefix.isEmpty()
                    ? entry.getKey()
                    : prefix + "." + entry.getKey();

            Object value = entry.getValue();

            if (value instanceof Map) {

                flattenMap(
                        key,
                        (Map<String, Object>) value,
                        properties
                );

            } else if (value != null) {

                properties.put(
                        key,
                        value.toString()
                );
            }
        }
    }


    /**
     * Crée automatiquement la base de données PostgreSQL
     * si elle n'existe pas encore.
     *
     * Se connecte à la base "postgres" (toujours présente)
     * puis exécute CREATE DATABASE si nécessaire.
     */
    private void autoCreateDatabase(
            ConfigurableEnvironment environment) {

        String url = environment.getProperty(
                "spring.datasource.url"
        );

        if (url == null || !url.contains("postgresql")) {
            return; // Pas PostgreSQL, on ne fait rien
        }

        // Résoudre les placeholders Spring (ex: ${MECI_DB_URL:...})
        url = environment.resolvePlaceholders(url);

        String username = environment.resolvePlaceholders(
                environment.getProperty(
                        "spring.datasource.username", "postgres"
                )
        );
        String password = environment.resolvePlaceholders(
                environment.getProperty(
                        "spring.datasource.password", ""
                )
        );

        // Extraire le nom de la base depuis l'URL JDBC
        // Format: jdbc:postgresql://host:port/dbname
        String dbName;
        try {
            String cleanUrl = url.replace("jdbc:", "");
            URI uri = new URI(cleanUrl);
            String path = uri.getPath();
            if (path == null || path.length() <= 1) {
                System.out.println(
                        "Impossible d'extraire le nom de la base depuis : "
                                + url
                );
                return;
            }
            dbName = path.substring(1); // Retirer le '/'

            // Retirer les paramètres éventuels (ex: ?ssl=true)
            if (dbName.contains("?")) {
                dbName = dbName.substring(0, dbName.indexOf("?"));
            }
        } catch (Exception e) {
            System.out.println(
                    "Format d'URL non reconnu : " + url
            );
            return;
        }

        // Construire l'URL pour se connecter à la base 'postgres'
        String adminUrl = url.substring(
                0, url.lastIndexOf("/")
        ) + "/postgres";

        try (Connection conn = DriverManager.getConnection(
                adminUrl, username, password)) {

            // Vérifier si la base existe
            boolean exists = false;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT 1 FROM pg_database WHERE datname = '"
                                 + dbName.replace("'", "''") + "'"
                 )) {
                exists = rs.next();
            }

            if (!exists) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(
                            "CREATE DATABASE \""
                                    + dbName.replace("\"", "\"\"")
                                    + "\""
                    );
                    System.out.println(
                            "✅ Base de données '" + dbName
                                    + "' créée automatiquement."
                    );
                }
            } else {
                System.out.println(
                        "Base de données '" + dbName
                                + "' existe déjà."
                );
            }

        } catch (Exception e) {
            System.out.println(
                    "⚠️ Impossible de créer la base de données '"
                            + dbName + "' automatiquement : "
                            + e.getMessage()
            );
            System.out.println(
                    "Créez-la manuellement avec : CREATE DATABASE "
                            + dbName + ";"
            );
        }
    }


    /**
     * Priorité maximale afin que la configuration externe
     * soit disponible avant la création du DataSource.
     */
    @Override
    public int getOrder() {

        return Ordered.HIGHEST_PRECEDENCE;
    }
}

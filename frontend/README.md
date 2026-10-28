# Backend Spring Boot - MECI E-SERVICES

Ce dossier contient l'application backend **Spring Boot** (Java 21) pour les services MECI, incluant l'API REST de **Gestion des Rapports** et l'espace **Admin Rapport**.

## Architecture & Fonctionnalités

- **Gestion des Rapports** (`/api/rapports`) : Création, consultation, filtrage par statut/type/mots-clés, mise à jour et suppression des rapports.
- **Espace Admin Rapport** (`/api/admin`) : Authentification administrateur, validation et changement de statut des rapports (Validé, En attente, Rejeté), suppression d'utilisateurs, et matrice dynamique d'autorisations de vérification (Checkboxes).
- **CORS** : Configuré pour autoriser le frontend Angular s'exécutant sur `http://localhost:4200`.

## Prérequis

- **Java JDK 21** ou supérieur
- **Maven** (utilisable via le wrapper `./mvnw` inclus)
- **PostgreSQL** avec une base `meci_eservices`

## Installation et Exécution

1. Ouvrir le dossier backend `e-service-meci` dans votre environnement de développement (IntelliJ IDEA, Eclipse, VS Code ou Terminal).
2. Vérifier ou compiler le projet :
   ```bash
   ./mvnw clean compile
   ```
3. Lancer l'application Spring Boot :
   ```bash
   ./mvnw spring-boot:run
   ```

L'application démarre par défaut sur le port **8080** :
- **URL Base API** : `http://localhost:8080/api`
- **Endpoints Rapports** : `http://localhost:8080/api/rapports`
- **Endpoints Admin** : `http://localhost:8080/api/admin/autorisations`

## Base de données

Le backend utilise PostgreSQL sur `localhost:5432`, dans la base `meci_eservices`. Les tables sont créées ou mises à jour au démarrage (`spring.jpa.hibernate.ddl-auto: update`) et se trouvent dans le schéma `public`.

La connexion peut être personnalisée avec les variables d'environnement `MECI_DB_URL`, `MECI_DB_USERNAME` et `MECI_DB_PASSWORD`. Par défaut, l'URL est `jdbc:postgresql://localhost:5432/meci_eservices` et l'utilisateur est `postgres`. Créez d'abord la base si elle n'existe pas, puis définissez le mot de passe PostgreSQL dans `MECI_DB_PASSWORD` avant de lancer l'application.

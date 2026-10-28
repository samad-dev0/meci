package meci_e_services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import meci_e_services.auth.AccessControlFilter;
import meci_e_services.auth.JwtAuthenticationFilter;
import meci_e_services.service.AccessControlService;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AccessControlService accessControlService
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(401, "Authentification requise"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(403, accessDeniedException.getMessage() != null
                                        ? accessDeniedException.getMessage()
                                        : "Accès refusé")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/", "/api", "/api/").permitAll()
                        .requestMatchers("/api/auth/**", "/api/admin/auth/login").permitAll()
                        .requestMatchers("/api/access-control/catalog").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs/*/access-rights").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs", "/api/utilisateurs/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/utilisateurs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/utilisateurs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/utilisateurs/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(
                        new AccessControlFilter(accessControlService),
                        JwtAuthenticationFilter.class
                );

        return http.build();
    }
}
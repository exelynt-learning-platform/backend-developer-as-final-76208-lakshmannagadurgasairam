package com.example.booking.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // Disable CSRF because this is a stateless REST API
            .csrf(AbstractHttpConfigurer::disable)

            // JWT-based authentication, no HTTP session
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // Authentication and authorization error handling
            .exceptionHandling(exception -> exception

                // No JWT / unauthenticated request
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"status\":401,\"message\":\"Authentication required\"}"
                    );
                })

                // Authenticated user without sufficient permissions
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"status\":403,\"message\":\"Access denied: insufficient privileges\"}"
                    );
                })
            )

            .authorizeHttpRequests(auth -> auth

                // ==========================================
                // PUBLIC ENDPOINTS
                // ==========================================
                .requestMatchers(
                    "/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html"
                ).permitAll()

                // ==========================================
                // RESOURCES
                // ==========================================

                // USER + ADMIN can view resources
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/resources/**"
                ).hasAnyRole("USER", "ADMIN")

                // ADMIN only can create resources
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/resources/**"
                ).hasRole("ADMIN")

                // ADMIN only can update resources
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/resources/**"
                ).hasRole("ADMIN")

                // ADMIN only can delete resources
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/resources/**"
                ).hasRole("ADMIN")

                // ==========================================
                // RESERVATIONS
                // ==========================================

                // USER + ADMIN can view reservations
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/reservations/**"
                ).hasAnyRole("USER", "ADMIN")

                // USER + ADMIN can create reservations
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/reservations/**"
                ).hasAnyRole("USER", "ADMIN")

                // ADMIN only can change reservation status
                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/reservations/**"
                ).hasRole("ADMIN")

                // ADMIN only can delete reservations
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/reservations/**"
                ).hasRole("ADMIN")

                // ==========================================
                // EVERYTHING ELSE
                // ==========================================
                .anyRequest().authenticated()
            )

            // Authentication provider
            .authenticationProvider(authenticationProvider())

            // JWT filter runs before Spring's username/password filter
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config)
            throws Exception {

        return config.getAuthenticationManager();
    }
}

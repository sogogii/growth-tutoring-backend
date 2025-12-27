package com.growthtutoring.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials
        config.setAllowCredentials(true);

        // Allow origins
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:8080",
                "http://localhost:8000",
                "http://localhost:3000"
        ));

        // Allow headers
        config.setAllowedHeaders(Arrays.asList("*"));

        // Allow methods
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // Max age
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/api/**", config);

        System.out.println("========================================");
        System.out.println("✓ CORS Filter Configured");
        System.out.println("  Allowed Origins: localhost:5173, 8080, 8000, 3000");
        System.out.println("  Allowed Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS");
        System.out.println("========================================");

        return new CorsFilter(source);
    }
}
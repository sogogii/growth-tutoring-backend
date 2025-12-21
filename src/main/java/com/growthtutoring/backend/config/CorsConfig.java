package com.growthtutoring.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration for Growth Tutoring Backend
 *
 * This allows the frontend (running on port 5173) to make requests to the backend API.
 *
 * IMPORTANT: Replace your existing CorsConfig.java with this version if the @Bean approach isn't working.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    public CorsConfig() {
        // This will print when Spring loads the configuration
        System.out.println("========================================");
        System.out.println("✓ CORS Configuration Loaded!");
        System.out.println("========================================");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("========================================");
        System.out.println("✓ Adding CORS Mappings:");
        System.out.println("  - Allowed Origins: 5173, 8080, 8000");
        System.out.println("  - Allowed Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS");
        System.out.println("========================================");

        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:8080",
                        "http://localhost:8000",
                        "https://dev.growthtutoringhq.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
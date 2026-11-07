package com.harsh.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Allow any localhost / loopback origin (covers IntelliJ's built-in web server on :63342, etc.)
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://[::1]:*"
        ));

        // Allow all headers
        config.setAllowedHeaders(List.of("*"));

        // Allow specific HTTP methods
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        // Expose headers to frontend
        config.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

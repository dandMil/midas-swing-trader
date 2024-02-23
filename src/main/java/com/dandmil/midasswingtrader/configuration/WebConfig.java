package com.dandmil.midasswingtrader.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/midas/crypto/get_all").allowedOrigins("http://localhost:3000");
        // Add additional mappings if needed for other endpoints
    }
}
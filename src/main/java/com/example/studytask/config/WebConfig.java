package com.example.studytask.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String allowedOrigin;
    private final DemoReadOnlyInterceptor demoReadOnlyInterceptor;

    public WebConfig(
            @Value("${app.cors.allowed-origin:http://localhost:5173}") String allowedOrigin,
            @Value("${app.demo.read-only:false}") boolean demoReadOnly,
            ObjectMapper objectMapper) {
        this.allowedOrigin = removeTrailingSlash(allowedOrigin);
        this.demoReadOnlyInterceptor = new DemoReadOnlyInterceptor(demoReadOnly, objectMapper);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Accept")
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(demoReadOnlyInterceptor)
                .addPathPatterns("/api/**");
    }

    private String removeTrailingSlash(String origin) {
        return origin == null ? "" : origin.replaceAll("/+$", "");
    }
}

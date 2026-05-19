package com.campus.market.config;

import com.campus.market.image.FileStorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileStorageProperties fileStorageProperties;

    public WebMvcConfig(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        var publicPath = normalizePublicPath(fileStorageProperties.publicPath());
        var uploadDir = Path.of(fileStorageProperties.uploadDir()).toAbsolutePath().normalize();
        registry.addResourceHandler(publicPath + "/**")
                .addResourceLocations(uploadDir.toUri().toString());
    }

    private String normalizePublicPath(String value) {
        var normalized = value == null || value.isBlank() ? "/uploads" : value.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        return normalized.endsWith("/") ? normalized.substring(0, normalized.length() - 1) : normalized;
    }
}

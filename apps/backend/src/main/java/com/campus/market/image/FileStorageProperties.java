package com.campus.market.image;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public record FileStorageProperties(
        String uploadDir,
        String publicPath
) {
}

package com.campus.market.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminBootstrapProperties(
        String username,
        String password,
        String nickname
) {
}

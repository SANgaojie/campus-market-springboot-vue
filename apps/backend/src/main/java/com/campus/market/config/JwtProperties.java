package com.campus.market.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JwtProperties 业务组件。
 *
 * @author 阿德
 * @date 2026/05/14
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String issuer,
        String secret,
        long expirationMinutes
) {
}

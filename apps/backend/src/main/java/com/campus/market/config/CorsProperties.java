package com.campus.market.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * CorsProperties 业务组件。
 *
 * @author 阿德
 * @date 2026/05/13
 */
@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(
        List<String> allowedOrigins
) {
}

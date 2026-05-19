package com.campus.market.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AdminBootstrapProperties 业务组件。
 *
 * @author 阿德
 * @date 2026/05/15
 */
@ConfigurationProperties(prefix = "app.admin")
public record AdminBootstrapProperties(
        String username,
        String password,
        String nickname
) {
}

package com.campus.market.image;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * FileStorageProperties 业务组件。
 *
 * @author 阿德
 * @date 2026/05/17
 */
@ConfigurationProperties(prefix = "app.storage")
public record FileStorageProperties(
        String uploadDir,
        String publicPath
) {
}

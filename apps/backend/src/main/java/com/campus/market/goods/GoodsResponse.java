package com.campus.market.goods;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * GoodsResponse 业务组件。
 *
 * @author 阿德
 * @date 2026/05/13
 */
public record GoodsResponse(
        Long id,
        Long sellerId,
        Long categoryId,
        String title,
        String description,
        BigDecimal price,
        Integer conditionLevel,
        GoodsStatus status,
        LocalDateTime createdAt,
        List<String> imageUrls
) {
}

package com.campus.market.goods;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

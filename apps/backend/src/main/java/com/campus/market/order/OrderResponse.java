package com.campus.market.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String orderNo,
        Long goodsId,
        Long buyerId,
        Long sellerId,
        BigDecimal amount,
        OrderStatus status,
        LocalDateTime createdAt
) {
}

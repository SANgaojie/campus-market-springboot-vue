package com.campus.market.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderResponse 业务组件。
 *
 * @author 阿德
 * @date 2026/05/12
 */
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

package com.campus.market.order;

/**
 * OrderStatus 业务组件。
 *
 * @author 阿德
 * @date 2026/05/09
 */
public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    CANCELED,
    COMPLETED,
    REFUNDING,
    REFUNDED
}

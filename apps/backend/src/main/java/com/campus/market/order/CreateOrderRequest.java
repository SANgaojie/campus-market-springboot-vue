package com.campus.market.order;

import jakarta.validation.constraints.NotNull;

/**
 * CreateOrderRequest 业务组件。
 *
 * @author 阿德
 * @date 2026/05/06
 */
public record CreateOrderRequest(
        @NotNull Long goodsId
) {
}

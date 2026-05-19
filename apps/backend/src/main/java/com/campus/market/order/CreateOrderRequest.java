package com.campus.market.order;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull Long goodsId
) {
}

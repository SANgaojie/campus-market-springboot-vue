package com.campus.market.goods;

public record CategoryResponse(
        Long id,
        String name,
        Integer sortOrder,
        Integer enabled
) {
}

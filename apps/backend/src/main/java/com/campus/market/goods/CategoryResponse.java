package com.campus.market.goods;

/**
 * CategoryResponse 业务组件。
 *
 * @author 阿德
 * @date 2026/05/17
 */
public record CategoryResponse(
        Long id,
        String name,
        Integer sortOrder,
        Integer enabled
) {
}

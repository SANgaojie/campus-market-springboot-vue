package com.campus.market.goods;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * CategoryManageRequest 业务组件。
 *
 * @author 阿德
 * @date 2026/05/07
 */
public record CategoryManageRequest(
        @NotBlank @Size(max = 50) String name,
        @NotNull @Min(0) Integer sortOrder
) {
}

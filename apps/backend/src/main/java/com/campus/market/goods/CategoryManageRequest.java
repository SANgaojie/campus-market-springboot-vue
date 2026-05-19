package com.campus.market.goods;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryManageRequest(
        @NotBlank @Size(max = 50) String name,
        @NotNull @Min(0) Integer sortOrder
) {
}

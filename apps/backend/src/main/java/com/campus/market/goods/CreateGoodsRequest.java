package com.campus.market.goods;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateGoodsRequest(
        @NotNull Long categoryId,
        @NotBlank @Size(max = 100) String title,
        @Size(max = 2000) String description,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotNull @Min(1) @Max(5) Integer conditionLevel,
        @Size(max = 9) List<@NotBlank @Size(max = 255) String> imageUrls
) {
}

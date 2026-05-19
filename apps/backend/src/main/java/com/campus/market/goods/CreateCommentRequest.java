package com.campus.market.goods;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CreateCommentRequest 业务组件。
 *
 * @author 阿德
 * @date 2026/05/13
 */
public record CreateCommentRequest(
        @NotBlank
        @Size(max = 500)
        String content
) {
}

package com.campus.market.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * RegisterRequest 业务组件。
 *
 * @author 阿德
 * @date 2026/05/13
 */
public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 6, max = 72) String password,
        @NotBlank @Size(max = 50) String nickname
) {
}

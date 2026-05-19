package com.campus.market.user;

import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest 业务组件。
 *
 * @author 阿德
 * @date 2026/05/08
 */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}

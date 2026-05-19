package com.campus.market.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ChangePasswordRequest 业务组件。
 *
 * @author 阿德
 * @date 2026/05/07
 */
public record ChangePasswordRequest(
        @NotBlank String oldPassword,
        @NotBlank @Size(min = 6, max = 72) String newPassword
) {
}

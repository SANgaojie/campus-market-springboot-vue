package com.campus.market.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * UpdateProfileRequest 业务组件。
 *
 * @author 阿德
 * @date 2026/05/14
 */
public record UpdateProfileRequest(
        @NotBlank @Size(max = 50) String nickname
) {
}

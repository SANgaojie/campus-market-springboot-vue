package com.campus.market.user;

/**
 * AuthResponse 业务组件。
 *
 * @author 阿德
 * @date 2026/05/07
 */
public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}

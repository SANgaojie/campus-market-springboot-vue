package com.campus.market.user;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}

package com.campus.market.user;

import java.util.List;

public record UserProfileResponse(
        Long id,
        String username,
        String nickname,
        Integer status,
        List<String> roles
) {
}

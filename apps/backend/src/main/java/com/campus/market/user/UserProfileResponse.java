package com.campus.market.user;

import java.util.List;

/**
 * UserProfileResponse 业务组件。
 *
 * @author 阿德
 * @date 2026/05/18
 */
public record UserProfileResponse(
        Long id,
        String username,
        String nickname,
        Integer status,
        List<String> roles
) {
}

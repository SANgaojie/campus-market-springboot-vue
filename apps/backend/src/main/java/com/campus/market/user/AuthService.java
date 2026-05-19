package com.campus.market.user;

import com.campus.market.common.BusinessException;
import com.campus.market.config.JwtTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService 业务组件。
 *
 * @author 阿德
 * @date 2026/05/06
 */
@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public AuthResponse register(RegisterRequest request) {
        var profile = userService.register(request);
        var token = jwtTokenService.createToken(profile.id(), profile.username(), profile.roles());
        return new AuthResponse(token, profile);
    }

    public AuthResponse login(LoginRequest request) {
        var user = userService.findEnabledByUsername(request.username());
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        var profile = userService.toProfile(user);
        var token = jwtTokenService.createToken(profile.id(), profile.username(), profile.roles());
        return new AuthResponse(token, profile);
    }

    public UserProfileResponse currentUser(String userId) {
        var user = userService.findById(Long.parseLong(userId));
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return userService.toProfile(user);
    }

    public UserProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        return userService.updateProfile(Long.parseLong(userId), request);
    }

    public void changePassword(String userId, ChangePasswordRequest request) {
        userService.changePassword(Long.parseLong(userId), request);
    }
}

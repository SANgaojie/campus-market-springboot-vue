package com.campus.market.user;

import com.campus.market.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> me(Authentication authentication) {
        return ApiResponse.ok(authService.currentUser(authentication.getName()));
    }

    @PatchMapping("/me")
    public ApiResponse<UserProfileResponse> updateMe(@Valid @RequestBody UpdateProfileRequest request,
                                                     Authentication authentication) {
        return ApiResponse.ok(authService.updateProfile(authentication.getName(), request));
    }

    @PatchMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                            Authentication authentication) {
        authService.changePassword(authentication.getName(), request);
        return ApiResponse.ok();
    }
}

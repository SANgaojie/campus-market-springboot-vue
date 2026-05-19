package com.campus.market.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 6, max = 72) String password,
        @NotBlank @Size(max = 50) String nickname
) {
}

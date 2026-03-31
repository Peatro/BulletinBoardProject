package com.peatroxd.bulletinboardproject.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
        @NotBlank
        String username,

        @NotBlank
        String password
) {
}

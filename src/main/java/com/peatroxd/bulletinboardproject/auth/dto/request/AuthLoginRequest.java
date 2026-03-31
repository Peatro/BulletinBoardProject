package com.peatroxd.bulletinboardproject.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на логин пользователя")
public record AuthLoginRequest(
        @Schema(description = "Username пользователя", example = "alice")
        @NotBlank
        String username,

        @Schema(description = "Пароль пользователя", example = "secret123")
        @NotBlank
        String password
) {
}

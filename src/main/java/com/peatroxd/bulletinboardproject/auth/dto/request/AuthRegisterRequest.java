package com.peatroxd.bulletinboardproject.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на регистрацию пользователя")
public record AuthRegisterRequest(
        @Schema(description = "Уникальный username пользователя", example = "alice")
        @NotBlank
        @Size(max = 100)
        String username,

        @Schema(description = "Email пользователя", example = "alice@example.com")
        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @Schema(description = "Имя пользователя", example = "Alice")
        @NotBlank
        @Size(max = 100)
        String firstName,

        @Schema(description = "Фамилия пользователя", example = "Smith", nullable = true)
        @Size(max = 100)
        String lastName,

        @Schema(description = "Телефон пользователя", example = "+70000000000", nullable = true)
        @Size(max = 30)
        String phone,

        @Schema(description = "Пароль пользователя", example = "secret123", minLength = 8, maxLength = 128)
        @NotBlank
        @Size(min = 8, max = 128)
        String password
) {
}

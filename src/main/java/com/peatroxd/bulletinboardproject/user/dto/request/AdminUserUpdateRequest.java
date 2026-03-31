package com.peatroxd.bulletinboardproject.user.dto.request;

import com.peatroxd.bulletinboardproject.security.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на обновление пользователя администратором")
public record AdminUserUpdateRequest(
        @Schema(description = "Username пользователя", example = "alice")
        @NotBlank
        @Size(max = 255)
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

        @Schema(description = "Роль пользователя", example = "ADMIN")
        @NotNull
        Role role,

        @Schema(description = "Флаг активности пользователя", example = "true")
        boolean enabled
) {
}

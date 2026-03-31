package com.peatroxd.bulletinboardproject.auth.dto.response;

import com.peatroxd.bulletinboardproject.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ после успешной регистрации пользователя")
public record AuthRegisterResponse(
        @Schema(description = "Username пользователя", example = "alice")
        String username,
        @Schema(description = "Email пользователя", example = "alice@example.com")
        String email,
        @Schema(description = "Имя пользователя", example = "Alice")
        String firstName,
        @Schema(description = "Фамилия пользователя", example = "Smith", nullable = true)
        String lastName,
        @Schema(description = "Телефон пользователя", example = "+70000000000", nullable = true)
        String phone
) {
    public static AuthRegisterResponse from(User user) {
        return new AuthRegisterResponse(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone()
        );
    }
}

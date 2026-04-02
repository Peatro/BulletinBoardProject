package com.peatroxd.bulletinboardproject.user.dto.response;

import com.peatroxd.bulletinboardproject.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Публичное представление пользователя")
public record UserResponse(
        @Schema(description = "Идентификатор пользователя в локальной БД", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
        UUID id,
        @Schema(description = "Идентификатор пользователя в Keycloak", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        UUID keycloakUserId,
        @Schema(description = "Username пользователя", example = "alice")
        String username,
        @Schema(description = "Email пользователя", example = "alice@example.com")
        String email,
        @Schema(description = "Имя пользователя", example = "Alice")
        String firstName,
        @Schema(description = "Фамилия пользователя", example = "Smith", nullable = true)
        String lastName,
        @Schema(description = "Телефон пользователя", example = "+70000000000", nullable = true)
        String phone,
        @Schema(description = "Роль пользователя", example = "USER")
        String role,
        @Schema(description = "Флаг активности пользователя", example = "true")
        boolean enabled
) {
    public static UserResponse from(User user) {
        String role = user.getRole() != null ? user.getRole().name() : null;

        return new UserResponse(
                user.getId(),
                user.getKeycloakUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                role,
                user.isEnabled()
        );
    }
}

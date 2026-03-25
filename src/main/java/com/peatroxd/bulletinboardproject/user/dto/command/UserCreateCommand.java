package com.peatroxd.bulletinboardproject.user.dto.command;

import com.peatroxd.bulletinboardproject.security.Role;

import java.util.UUID;

public record UserCreateCommand(
        UUID keycloakUserId,
        String username,
        String email,
        String firstName,
        String lastName,
        String phone,
        Role role,
        boolean enabled
) {
}

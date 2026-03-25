package com.peatroxd.bulletinboardproject.auth.dto.response;

import com.peatroxd.bulletinboardproject.user.entity.User;

public record AuthRegisterResponse(
        String username,
        String email,
        String firstName,
        String lastName,
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

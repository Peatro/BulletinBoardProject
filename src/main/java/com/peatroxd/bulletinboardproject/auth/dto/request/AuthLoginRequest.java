package com.peatroxd.bulletinboardproject.auth.dto.request;

public record AuthLoginRequest(
        String username,
        String password
) {
}

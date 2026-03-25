package com.peatroxd.bulletinboardproject.auth.service.impl;

import com.peatroxd.bulletinboardproject.auth.dto.request.AuthLoginRequest;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthTokenResponse;
import com.peatroxd.bulletinboardproject.auth.service.AuthService;
import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.security.keycloak.KeycloakAuthClient;
import com.peatroxd.bulletinboardproject.auth.dto.request.AuthRegisterRequest;
import com.peatroxd.bulletinboardproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final KeycloakAuthClient keycloakAuthClient;

    @Override
    public void register(AuthRegisterRequest request) {
        userService.createUser(request, Role.USER);
    }

    @Override
    public AuthTokenResponse login(AuthLoginRequest request) {
        validateLoginRequest(request);
        return keycloakAuthClient.login(request);
    }

    private void validateLoginRequest(AuthLoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Login request must not be null");
        }
        if (!StringUtils.hasText(request.username())) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (!StringUtils.hasText(request.password())) {
            throw new IllegalArgumentException("Password must not be blank");
        }
    }
}

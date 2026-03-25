package com.peatroxd.bulletinboardproject.auth.service.impl;

import com.peatroxd.bulletinboardproject.auth.dto.request.AuthLoginRequest;
import com.peatroxd.bulletinboardproject.auth.dto.request.AuthRegisterRequest;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthRegisterResponse;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthTokenResponse;
import com.peatroxd.bulletinboardproject.auth.service.AuthService;
import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.security.keycloak.KeycloakAuthClient;
import com.peatroxd.bulletinboardproject.security.keycloak.KeycloakAdminClient;
import com.peatroxd.bulletinboardproject.user.dto.command.UserCreateCommand;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserFacade userFacade;
    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakAdminClient keycloakAdminClient;

    @Override
    public AuthRegisterResponse register(AuthRegisterRequest request) {
        validateRegisterRequest(request);
        UUID keycloakUserId = keycloakAdminClient.createUser(
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phone(),
                request.password(),
                Role.USER
        );

        try {
            UserCreateCommand command = new UserCreateCommand(
                    keycloakUserId,
                    request.username(),
                    request.email(),
                    request.firstName(),
                    request.lastName(),
                    request.phone(),
                    Role.USER,
                    true
            );

            User user = userFacade.createUser(command);
            return AuthRegisterResponse.from(user);
        } catch (RuntimeException ex) {
            try {
                keycloakAdminClient.deleteUser(keycloakUserId);
            } catch (RuntimeException ignored) {
            }
            throw ex;
        }
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

    private void validateRegisterRequest(AuthRegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Register request must not be null");
        }
        if (!StringUtils.hasText(request.username())) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (!StringUtils.hasText(request.email())) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (!StringUtils.hasText(request.firstName())) {
            throw new IllegalArgumentException("First name must not be blank");
        }
        if (!StringUtils.hasText(request.password())) {
            throw new IllegalArgumentException("Password must not be blank");
        }
    }
}

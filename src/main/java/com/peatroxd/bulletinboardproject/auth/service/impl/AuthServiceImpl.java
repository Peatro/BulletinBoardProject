package com.peatroxd.bulletinboardproject.auth.service.impl;

import com.peatroxd.bulletinboardproject.auth.dto.request.AuthLoginRequest;
import com.peatroxd.bulletinboardproject.auth.dto.request.AuthRegisterRequest;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthRegisterResponse;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthTokenResponse;
import com.peatroxd.bulletinboardproject.auth.service.AuthService;
import com.peatroxd.bulletinboardproject.common.exception.RegistrationCompensationException;
import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.security.keycloak.KeycloakAdminClient;
import com.peatroxd.bulletinboardproject.security.keycloak.KeycloakAuthClient;
import com.peatroxd.bulletinboardproject.user.dto.command.UserCreateCommand;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.facade.UserFacade;
import com.peatroxd.bulletinboardproject.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserFacade userFacade;
    private final UserMapper userMapper;
    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakAdminClient keycloakAdminClient;

    @Override
    public AuthRegisterResponse register(AuthRegisterRequest request) {
        UUID keycloakUserId = registerKeycloakUser(request);

        try {
            return persistLocalUser(request, keycloakUserId);
        } catch (RuntimeException persistenceException) {
            compensateFailedRegistration(keycloakUserId, persistenceException);
            throw persistenceException;
        }
    }

    @Override
    public AuthTokenResponse login(AuthLoginRequest request) {
        return keycloakAuthClient.login(request);
    }

    private UUID registerKeycloakUser(AuthRegisterRequest request) {
        return keycloakAdminClient.createUser(
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phone(),
                request.password(),
                Role.USER
        );
    }

    private AuthRegisterResponse persistLocalUser(AuthRegisterRequest request, UUID keycloakUserId) {
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
        return userMapper.toRegisterResponse(user);
    }

    private void compensateFailedRegistration(UUID keycloakUserId, RuntimeException persistenceException) {
        try {
            keycloakAdminClient.deleteUser(keycloakUserId);
        } catch (RuntimeException compensationException) {
            RegistrationCompensationException exception = new RegistrationCompensationException(
                    "Failed to compensate Keycloak user after local persistence error",
                    persistenceException
            );
            exception.addSuppressed(compensationException);
            throw exception;
        }
    }
}

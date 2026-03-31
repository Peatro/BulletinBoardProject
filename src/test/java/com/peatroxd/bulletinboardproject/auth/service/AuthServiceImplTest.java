package com.peatroxd.bulletinboardproject.auth.service;

import com.peatroxd.bulletinboardproject.auth.dto.request.AuthLoginRequest;
import com.peatroxd.bulletinboardproject.auth.dto.request.AuthRegisterRequest;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthRegisterResponse;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthTokenResponse;
import com.peatroxd.bulletinboardproject.auth.service.impl.AuthServiceImpl;
import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.security.keycloak.KeycloakAdminClient;
import com.peatroxd.bulletinboardproject.security.keycloak.KeycloakAuthClient;
import com.peatroxd.bulletinboardproject.user.entity.User;
import com.peatroxd.bulletinboardproject.user.facade.UserFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String USERNAME = "alice";
    private static final String EMAIL = "alice@example.com";
    private static final String FIRST_NAME = "Alice";
    private static final String LAST_NAME = "Smith";
    private static final String PHONE = "+70000000000";
    private static final String PASSWORD = "secret123";

    @Mock
    private UserFacade userFacade;

    @Mock
    private KeycloakAuthClient keycloakAuthClient;

    @Mock
    private KeycloakAdminClient keycloakAdminClient;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registerShouldCreateKeycloakUserAndPersistLocalUser() {
        AuthRegisterRequest request = registerRequest();
        UUID keycloakUserId = UUID.randomUUID();
        User persistedUser = user(keycloakUserId);

        mockKeycloakUserCreation(request, keycloakUserId);
        when(userFacade.createUser(any())).thenReturn(persistedUser);

        AuthRegisterResponse response = authService.register(request);

        assertThat(response).isEqualTo(AuthRegisterResponse.from(persistedUser));

        verifyKeycloakUserCreation(request);
        verify(userFacade).createUser(any());
        verify(keycloakAdminClient, never()).deleteUser(any());
    }

    @Test
    void registerShouldDeleteKeycloakUserWhenLocalPersistenceFails() {
        AuthRegisterRequest request = registerRequest();
        UUID keycloakUserId = UUID.randomUUID();

        mockKeycloakUserCreation(request, keycloakUserId);
        when(userFacade.createUser(any())).thenThrow(new IllegalStateException("DB failure"));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("DB failure");

        verify(keycloakAdminClient).deleteUser(keycloakUserId);
    }

    @Test
    void registerShouldRejectBlankUsername() {
        AuthRegisterRequest request = new AuthRegisterRequest(
                " ",
                EMAIL,
                FIRST_NAME,
                LAST_NAME,
                PHONE,
                PASSWORD
        );

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username must not be blank");

        verify(keycloakAdminClient, never()).createUser(any(), any(), any(), any(), any(), any(), any());
        verify(userFacade, never()).createUser(any());
    }

    @Test
    void loginShouldDelegateToKeycloakAuthClient() {
        AuthLoginRequest request = loginRequest();
        AuthTokenResponse tokenResponse = tokenResponse();

        when(keycloakAuthClient.login(request)).thenReturn(tokenResponse);

        AuthTokenResponse response = authService.login(request);

        assertThat(response).isEqualTo(tokenResponse);
        verify(keycloakAuthClient).login(request);
    }

    @Test
    void loginShouldRejectBlankPassword() {
        AuthLoginRequest request = new AuthLoginRequest(USERNAME, " ");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password must not be blank");

        verify(keycloakAuthClient, never()).login(any());
    }

    private AuthRegisterRequest registerRequest() {
        return new AuthRegisterRequest(USERNAME, EMAIL, FIRST_NAME, LAST_NAME, PHONE, PASSWORD);
    }

    private AuthLoginRequest loginRequest() {
        return new AuthLoginRequest(USERNAME, PASSWORD);
    }

    private AuthTokenResponse tokenResponse() {
        return new AuthTokenResponse(
                "access-token",
                "refresh-token",
                "Bearer",
                300L,
                1800L,
                "openid profile"
        );
    }

    private User user(UUID keycloakUserId) {
        return User.builder()
                .id(UUID.randomUUID())
                .keycloakUserId(keycloakUserId)
                .username(USERNAME)
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .phone(PHONE)
                .role(Role.USER)
                .enabled(true)
                .build();
    }

    private void mockKeycloakUserCreation(AuthRegisterRequest request, UUID keycloakUserId) {
        when(keycloakAdminClient.createUser(
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phone(),
                request.password(),
                Role.USER
        )).thenReturn(keycloakUserId);
    }

    private void verifyKeycloakUserCreation(AuthRegisterRequest request) {
        verify(keycloakAdminClient).createUser(
                request.username(),
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phone(),
                request.password(),
                Role.USER
        );
    }
}

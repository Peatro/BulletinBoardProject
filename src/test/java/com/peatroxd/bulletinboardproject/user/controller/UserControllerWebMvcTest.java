package com.peatroxd.bulletinboardproject.user.controller;

import com.peatroxd.bulletinboardproject.common.exception.GlobalExceptionHandler;
import com.peatroxd.bulletinboardproject.security.service.CurrentUserArgumentResolver;
import com.peatroxd.bulletinboardproject.security.service.CurrentUserService;
import com.peatroxd.bulletinboardproject.user.controller.impl.UserControllerImpl;
import com.peatroxd.bulletinboardproject.user.dto.request.AdminUserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.request.UserUpdateRequest;
import com.peatroxd.bulletinboardproject.user.dto.response.UserResponse;
import com.peatroxd.bulletinboardproject.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerWebMvcTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserControllerImpl userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new CurrentUserArgumentResolver(new CurrentUserService()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserShouldReturnAuthenticatedUserProfile() throws Exception {
        UUID keycloakUserId = UUID.randomUUID();
        setCurrentJwtUser(keycloakUserId);
        UserResponse response = userResponse(keycloakUserId);

        when(userService.getCurrentUser(keycloakUserId)).thenReturn(response);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService).getCurrentUser(keycloakUserId);
    }

    @Test
    void updateCurrentUserShouldReturnUpdatedProfile() throws Exception {
        UUID keycloakUserId = UUID.randomUUID();
        setCurrentJwtUser(keycloakUserId);
        UserUpdateRequest request = new UserUpdateRequest(
                "updated@example.com",
                "Alice",
                "Johnson",
                "+79990000000"
        );
        UserResponse response = new UserResponse(
                UUID.randomUUID(),
                keycloakUserId,
                "alice",
                "updated@example.com",
                "Alice",
                "Johnson",
                "+79990000000",
                "USER",
                true
        );

        when(userService.updateCurrentUser(keycloakUserId, request)).thenReturn(response);

        mockMvc.perform(put("/api/users/me")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "updated@example.com",
                                  "firstName": "Alice",
                                  "lastName": "Johnson",
                                  "phone": "+79990000000"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.lastName").value("Johnson"))
                .andExpect(jsonPath("$.phone").value("+79990000000"));

        verify(userService).updateCurrentUser(keycloakUserId, request);
    }

    @Test
    void getUserByIdShouldReturnDtoForAdminEndpoint() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponse response = userResponse(UUID.randomUUID());

        when(userService.getUser(userId)).thenReturn(response);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService).getUser(userId);
    }

    @Test
    void updateUserShouldAcceptAdminDtoBody() throws Exception {
        UUID userId = UUID.randomUUID();
        AdminUserUpdateRequest request = new AdminUserUpdateRequest(
                "moderator",
                "moderator@example.com",
                "Mila",
                "Brown",
                "+79991112233",
                com.peatroxd.bulletinboardproject.security.Role.ADMIN,
                false
        );
        UserResponse response = new UserResponse(
                userId,
                UUID.randomUUID(),
                "moderator",
                "moderator@example.com",
                "Mila",
                "Brown",
                "+79991112233",
                "ADMIN",
                false
        );

        when(userService.updateUser(userId, request)).thenReturn(response);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "moderator",
                                  "email": "moderator@example.com",
                                  "firstName": "Mila",
                                  "lastName": "Brown",
                                  "phone": "+79991112233",
                                  "role": "ADMIN",
                                  "enabled": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("moderator"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.enabled").value(false));

        verify(userService).updateUser(userId, request);
    }

    private void setCurrentJwtUser(UUID userId) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(userId.toString())
                .claim("scope", "openid")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(jwt, null, List.of())
        );
    }

    private UserResponse userResponse(UUID keycloakUserId) {
        return new UserResponse(
                UUID.randomUUID(),
                keycloakUserId,
                "alice",
                "alice@example.com",
                "Alice",
                "Smith",
                "+70000000000",
                "USER",
                true
        );
    }
}

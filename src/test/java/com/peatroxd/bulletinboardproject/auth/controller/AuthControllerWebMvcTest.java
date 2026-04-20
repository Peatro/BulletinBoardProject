package com.peatroxd.bulletinboardproject.auth.controller;

import com.peatroxd.bulletinboardproject.auth.dto.request.AuthLoginRequest;
import com.peatroxd.bulletinboardproject.auth.dto.request.AuthRegisterRequest;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthRegisterResponse;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthTokenResponse;
import com.peatroxd.bulletinboardproject.auth.service.AuthService;
import com.peatroxd.bulletinboardproject.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerWebMvcTest {

    private static final String REGISTER_REQUEST_JSON = """
            {
              "username": "alice",
              "email": "alice@example.com",
              "firstName": "Alice",
              "lastName": "Smith",
              "phone": "+70000000000",
              "password": "secret123"
            }
            """;

    private static final String LOGIN_REQUEST_JSON = """
            {
              "username": "alice",
              "password": "secret123"
            }
            """;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerShouldReturnCreatedResponse() throws Exception {
        AuthRegisterRequest request = registerRequest();
        AuthRegisterResponse response = registerResponse();

        when(authService.register(request)).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        verify(authService).register(request);
    }

    @Test
    void registerShouldReturn400WhenUsernameIsBlank() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "",
                                  "email": "alice@example.com",
                                  "firstName": "Alice",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void registerShouldReturn400WhenPasswordIsTooShort() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "email": "alice@example.com",
                                  "firstName": "Alice",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void registerShouldReturn400WhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "email": "not-an-email",
                                  "firstName": "Alice",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void loginShouldReturnTokenResponse() throws Exception {
        AuthLoginRequest request = loginRequest();
        AuthTokenResponse response = tokenResponse();

        when(authService.login(request)).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_REQUEST_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access-token"))
                .andExpect(jsonPath("$.token_type").value("Bearer"));

        verify(authService).login(request);
    }

    @Test
    void loginShouldReturn400WhenPasswordIsBlank() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    @Test
    void loginShouldReturn401WhenCredentialsAreInvalid() throws Exception {
        AuthLoginRequest request = loginRequest();

        when(authService.login(request)).thenThrow(new com.peatroxd.bulletinboardproject.common.exception.UnauthorizedException("Invalid username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_REQUEST_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    private AuthRegisterRequest registerRequest() {
        return new AuthRegisterRequest(
                "alice",
                "alice@example.com",
                "Alice",
                "Smith",
                "+70000000000",
                "secret123"
        );
    }

    private AuthRegisterResponse registerResponse() {
        return new AuthRegisterResponse(
                "alice",
                "alice@example.com",
                "Alice",
                "Smith",
                "+70000000000"
        );
    }

    private AuthLoginRequest loginRequest() {
        return new AuthLoginRequest("alice", "secret123");
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
}

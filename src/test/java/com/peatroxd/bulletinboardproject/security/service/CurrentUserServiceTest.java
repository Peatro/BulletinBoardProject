package com.peatroxd.bulletinboardproject.security.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentUserServiceTest {

    private final CurrentUserService currentUserService = new CurrentUserService();
    private static final String USERNAME = "alice";

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUserIdShouldReadJwtSubject() {
        UUID userId = UUID.randomUUID();
        authenticate(jwt(userId, USERNAME));

        assertThat(currentUserService.getUserId()).isEqualTo(userId);
    }

    @Test
    void getUsernameShouldReadPreferredUsernameClaim() {
        authenticate(jwt(UUID.randomUUID(), USERNAME));

        assertThat(currentUserService.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void getUserIdShouldFailWhenPrincipalIsNotJwt() {
        authenticate("principal");

        assertThatThrownBy(currentUserService::getUserId)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No JWT found in security context");
    }

    private Jwt jwt(UUID userId, String username) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(userId.toString())
                .claim("preferred_username", username)
                .build();
    }

    private void authenticate(Object principal) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of())
        );
    }
}

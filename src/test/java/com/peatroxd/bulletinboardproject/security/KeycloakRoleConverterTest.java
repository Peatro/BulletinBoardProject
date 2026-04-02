package com.peatroxd.bulletinboardproject.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakRoleConverterTest {

    private final KeycloakRoleConverter converter = new KeycloakRoleConverter();
    private static final String SUBJECT = "user-1";

    @Test
    void convertShouldMapKnownRealmRolesToAuthorities() {
        Jwt jwt = jwtWithRealmRoles("user", "admin");

        assertThat(authorityNames(jwt))
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void convertShouldIgnoreUnknownRoles() {
        Jwt jwt = jwtWithRealmRoles("viewer", "manager");

        assertThat(authorityNames(jwt)).containsExactly("ROLE_VIEWER");
    }

    @Test
    void convertShouldReturnEmptyListWhenRealmAccessIsMissing() {
        Jwt jwt = jwtWithoutRoles();

        assertThat(converter.convert(jwt)).isEmpty();
    }

    private Jwt jwtWithRealmRoles(String... roles) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", SUBJECT)
                .claim("realm_access", Map.of("roles", List.of(roles)))
                .build();
    }

    private Jwt jwtWithoutRoles() {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", SUBJECT)
                .build();
    }

    private List<String> authorityNames(Jwt jwt) {
        return converter.convert(jwt)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}

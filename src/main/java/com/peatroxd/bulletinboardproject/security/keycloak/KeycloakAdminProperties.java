package com.peatroxd.bulletinboardproject.security.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak.admin")
public record KeycloakAdminProperties(
        String serverUrl,
        String realm,
        String tokenRealm,
        String clientId,
        String clientSecret,
        String username,
        String password
) {
}

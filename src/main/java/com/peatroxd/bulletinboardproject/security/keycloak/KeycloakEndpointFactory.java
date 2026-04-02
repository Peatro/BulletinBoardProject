package com.peatroxd.bulletinboardproject.security.keycloak;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
public class KeycloakEndpointFactory {

    private final KeycloakAdminProperties properties;

    public KeycloakEndpointFactory(KeycloakAdminProperties properties) {
        this.properties = properties;
    }

    public String usersUrl() {
        return baseUrl() + "/admin/realms/" + realm() + "/users";
    }

    public String userUrl(UUID userId) {
        return usersUrl() + "/" + userId;
    }

    public String realmRoleUrl(String roleName) {
        return baseUrl() + "/admin/realms/" + realm() + "/roles/" + roleName;
    }

    public String userRealmRoleMappingsUrl(UUID userId) {
        return userUrl(userId) + "/role-mappings/realm";
    }

    public String realmTokenUrl() {
        return baseUrl() + "/realms/" + realm() + "/protocol/openid-connect/token";
    }

    public String adminTokenUrl() {
        return baseUrl() + "/realms/" + tokenRealm() + "/protocol/openid-connect/token";
    }

    public String clientId() {
        String clientId = properties.clientId();
        if (!StringUtils.hasText(clientId)) {
            throw new IllegalStateException("keycloak.admin.client-id is not set");
        }
        return clientId;
    }

    public String clientSecret() {
        String clientSecret = properties.clientSecret();
        if (!StringUtils.hasText(clientSecret)) {
            throw new IllegalStateException("keycloak.admin.client-secret is not set");
        }
        return clientSecret;
    }

    public String realm() {
        String realm = properties.realm();
        if (!StringUtils.hasText(realm)) {
            throw new IllegalStateException("keycloak.admin.realm is not set");
        }
        return realm;
    }

    public String tokenRealm() {
        if (StringUtils.hasText(properties.tokenRealm())) {
            return properties.tokenRealm();
        }
        return realm();
    }

    public String baseUrl() {
        String baseUrl = properties.serverUrl();
        if (!StringUtils.hasText(baseUrl)) {
            throw new IllegalStateException("keycloak.admin.server-url is not set");
        }
        return baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
    }
}

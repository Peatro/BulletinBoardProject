package com.peatroxd.bulletinboardproject.security.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.peatroxd.bulletinboardproject.security.Role;
import com.peatroxd.bulletinboardproject.auth.dto.request.AuthRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KeycloakAdminClient {

    private final KeycloakAdminProperties properties;
    private final RestTemplate restTemplate;

    public UUID createUser(AuthRegisterRequest request, Role role) {
        String token = getAccessToken();
        UUID userId = createKeycloakUser(request, token);
        assignRealmRole(userId, role.name(), token);
        return userId;
    }

    public void deleteUser(UUID userId) {
        String url = normalizeBaseUrl() + "/admin/realms/" + requireRealm() + "/users/" + userId;
        HttpHeaders headers = bearerHeaders(getAccessToken());
        restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
    }

    private UUID createKeycloakUser(AuthRegisterRequest request, String token) {
        String url = normalizeBaseUrl() + "/admin/realms/" + requireRealm() + "/users";

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("username", request.username());
        payload.put("email", request.email());
        payload.put("enabled", true);
        payload.put("emailVerified", true);
        payload.put("requiredActions", List.of());

        if (StringUtils.hasText(request.firstName())) {
            payload.put("firstName", request.firstName());
        }

        Map<String, List<String>> attributes = new HashMap<>();
        if (StringUtils.hasText(request.phone())) {
            attributes.put("phone", List.of(request.phone()));
        }
        if (!attributes.isEmpty()) {
            payload.put("attributes", attributes);
        }

        Map<String, Object> credential = new LinkedHashMap<>();
        credential.put("type", "password");
        credential.put("value", request.password());
        credential.put("temporary", false);
        payload.put("credentials", List.of(credential));

        HttpHeaders headers = bearerHeaders(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(payload, headers),
                Void.class
        );

        URI location = response.getHeaders().getLocation();
        if (location == null) {
            throw new IllegalStateException("Keycloak did not return Location header for new user");
        }

        return UUID.fromString(extractIdFromLocation(location));
    }

    private void assignRealmRole(UUID userId, String roleName, String token) {
        Map<String, Object> roleRepresentation = getRealmRole(roleName, token);
        Object roleId = roleRepresentation.get("id");
        Object roleNameValue = roleRepresentation.get("name");

        if (roleId == null || roleNameValue == null) {
            throw new IllegalStateException("Keycloak role not found: " + roleName);
        }

        List<Map<String, Object>> roles = List.of(
                Map.of(
                        "id", roleId,
                        "name", roleNameValue
                )
        );

        String url = normalizeBaseUrl()
                + "/admin/realms/" + requireRealm()
                + "/users/" + userId
                + "/role-mappings/realm";

        HttpHeaders headers = bearerHeaders(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(roles, headers),
                Void.class
        );
    }

    private Map<String, Object> getRealmRole(String roleName, String token) {
        String url = normalizeBaseUrl() + "/admin/realms/" + requireRealm() + "/roles/" + roleName;
        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(bearerHeaders(token)),
                Map.class
        );
        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("Keycloak role response is empty for role: " + roleName);
        }
        return body;
    }

    private String getAccessToken() {
        String tokenUrl = normalizeBaseUrl()
                + "/realms/" + requireTokenRealm()
                + "/protocol/openid-connect/token";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        String clientId = requireClientId();

        form.add("client_id", clientId);

        if (StringUtils.hasText(properties.clientSecret())) {
            form.add("grant_type", "client_credentials");
            form.add("client_secret", properties.clientSecret());
        } else if (StringUtils.hasText(properties.username()) && StringUtils.hasText(properties.password())) {
            form.add("grant_type", "password");
            form.add("username", properties.username());
            form.add("password", properties.password());
        } else {
            throw new IllegalStateException("Keycloak admin credentials are not configured");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                new HttpEntity<>(form, headers),
                TokenResponse.class
        );

        TokenResponse body = response.getBody();
        if (body == null || !StringUtils.hasText(body.accessToken())) {
            throw new IllegalStateException("Keycloak access token is missing");
        }

        return body.accessToken();
    }

    private HttpHeaders bearerHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private String normalizeBaseUrl() {
        String baseUrl = properties.serverUrl();
        if (!StringUtils.hasText(baseUrl)) {
            throw new IllegalStateException("keycloak.admin.server-url is not set");
        }
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    private String requireRealm() {
        String realm = properties.realm();
        if (!StringUtils.hasText(realm)) {
            throw new IllegalStateException("keycloak.admin.realm is not set");
        }
        return realm;
    }

    private String requireTokenRealm() {
        if (StringUtils.hasText(properties.tokenRealm())) {
            return properties.tokenRealm();
        }
        return requireRealm();
    }

    private String requireClientId() {
        String clientId = properties.clientId();
        if (!StringUtils.hasText(clientId)) {
            throw new IllegalStateException("keycloak.admin.client-id is not set");
        }
        return clientId;
    }

    private static String extractIdFromLocation(URI location) {
        String value = location.toString();
        int lastSlash = value.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash == value.length() - 1) {
            throw new IllegalStateException("Unexpected Keycloak Location header: " + value);
        }
        return value.substring(lastSlash + 1);
    }

    private record TokenResponse(@JsonProperty("access_token") String accessToken) {
    }
}

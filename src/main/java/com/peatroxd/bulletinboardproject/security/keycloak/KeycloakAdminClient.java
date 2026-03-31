package com.peatroxd.bulletinboardproject.security.keycloak;

import com.peatroxd.bulletinboardproject.security.Role;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class KeycloakAdminClient {

    private final KeycloakEndpointFactory endpointFactory;
    private final KeycloakAdminTokenProvider adminTokenProvider;
    private final RestTemplate restTemplate;

    public KeycloakAdminClient(
            KeycloakEndpointFactory endpointFactory,
            KeycloakAdminTokenProvider adminTokenProvider,
            RestTemplate restTemplate
    ) {
        this.endpointFactory = endpointFactory;
        this.adminTokenProvider = adminTokenProvider;
        this.restTemplate = restTemplate;
    }

    public UUID createUser(
            String username,
            String email,
            String firstName,
            String lastName,
            String phone,
            String password,
            Role role
    ) {
        String token = adminTokenProvider.getAccessToken();
        UUID userId = createKeycloakUser(username, email, firstName, lastName, phone, password, token);
        assignRealmRole(userId, role.name(), token);
        return userId;
    }

    public void deleteUser(UUID userId) {
        HttpHeaders headers = bearerHeaders(adminTokenProvider.getAccessToken());
        restTemplate.exchange(
                endpointFactory.userUrl(userId),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
    }

    private UUID createKeycloakUser(
            String username,
            String email,
            String firstName,
            String lastName,
            String phone,
            String password,
            String token
    ) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("username", username);
        payload.put("firstName", firstName);
        payload.put("email", email);
        payload.put("enabled", true);

        if (StringUtils.hasText(lastName)) {
            payload.put("lastName", lastName);
        }

        Map<String, List<String>> attributes = new HashMap<>();
        if (StringUtils.hasText(phone)) {
            attributes.put("phone", List.of(phone));
        }
        if (!attributes.isEmpty()) {
            payload.put("attributes", attributes);
        }

        Map<String, Object> credential = new LinkedHashMap<>();
        credential.put("type", "password");
        credential.put("value", password);
        credential.put("temporary", false);
        payload.put("credentials", List.of(credential));

        HttpHeaders headers = bearerHeaders(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Void> response = restTemplate.exchange(
                endpointFactory.usersUrl(),
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

        HttpHeaders headers = bearerHeaders(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.exchange(
                endpointFactory.userRealmRoleMappingsUrl(userId),
                HttpMethod.POST,
                new HttpEntity<>(roles, headers),
                Void.class
        );
    }

    private Map<String, Object> getRealmRole(String roleName, String token) {
        ResponseEntity<Map> response = restTemplate.exchange(
                endpointFactory.realmRoleUrl(roleName),
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

    private HttpHeaders bearerHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private static String extractIdFromLocation(URI location) {
        String value = location.toString();
        int lastSlash = value.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash == value.length() - 1) {
            throw new IllegalStateException("Unexpected Keycloak Location header: " + value);
        }
        return value.substring(lastSlash + 1);
    }
}

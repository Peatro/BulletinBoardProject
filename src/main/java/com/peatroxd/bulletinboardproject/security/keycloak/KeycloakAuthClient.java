package com.peatroxd.bulletinboardproject.security.keycloak;

import com.peatroxd.bulletinboardproject.auth.dto.request.AuthLoginRequest;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KeycloakAuthClient {

    private final KeycloakAdminProperties properties;
    private final RestTemplate restTemplate;

    public AuthTokenResponse login(AuthLoginRequest request) {
        String tokenUrl = normalizeBaseUrl()
                + "/realms/" + requireRealm()
                + "/protocol/openid-connect/token";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", requireClientId());
        form.add("client_secret", requireClientSecret());
        form.add("username", request.username());
        form.add("password", request.password());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<AuthTokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(form, headers),
                    AuthTokenResponse.class
            );

            AuthTokenResponse body = response.getBody();
            if (body == null || !StringUtils.hasText(body.accessToken())) {
                throw new IllegalStateException("Keycloak token response is empty");
            }

            return body;
        } catch (HttpClientErrorException.Unauthorized ex) {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    private String normalizeBaseUrl() {
        String baseUrl = properties.serverUrl();
        if (!StringUtils.hasText(baseUrl)) {
            throw new IllegalStateException("keycloak.admin.server-url is not set");
        }
        return baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
    }

    private String requireRealm() {
        String realm = properties.realm();
        if (!StringUtils.hasText(realm)) {
            throw new IllegalStateException("keycloak.admin.realm is not set");
        }
        return realm;
    }

    private String requireClientId() {
        String clientId = properties.clientId();
        if (!StringUtils.hasText(clientId)) {
            throw new IllegalStateException("keycloak.admin.client-id is not set");
        }
        return clientId;
    }

    private String requireClientSecret() {
        String clientSecret = properties.clientSecret();
        if (!StringUtils.hasText(clientSecret)) {
            throw new IllegalStateException("keycloak.admin.client-secret is not set");
        }
        return clientSecret;
    }
}
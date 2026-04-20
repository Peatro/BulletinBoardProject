package com.peatroxd.bulletinboardproject.security.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class KeycloakAdminTokenProvider {

    private final KeycloakAdminProperties properties;
    private final KeycloakEndpointFactory endpointFactory;
    private final RestTemplate restTemplate;

    public KeycloakAdminTokenProvider(
            KeycloakAdminProperties properties,
            KeycloakEndpointFactory endpointFactory,
            RestTemplate restTemplate
    ) {
        this.properties = properties;
        this.endpointFactory = endpointFactory;
        this.restTemplate = restTemplate;
    }

    public String getAccessToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", endpointFactory.clientId());

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
                endpointFactory.adminTokenUrl(),
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

    private record TokenResponse(@JsonProperty("access_token") String accessToken) {
    }
}

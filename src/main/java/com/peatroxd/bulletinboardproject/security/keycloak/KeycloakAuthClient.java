package com.peatroxd.bulletinboardproject.security.keycloak;

import com.peatroxd.bulletinboardproject.auth.dto.request.AuthLoginRequest;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthTokenResponse;
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
public class KeycloakAuthClient {

    private final KeycloakEndpointFactory endpointFactory;
    private final RestTemplate restTemplate;

    public KeycloakAuthClient(KeycloakEndpointFactory endpointFactory, RestTemplate restTemplate) {
        this.endpointFactory = endpointFactory;
        this.restTemplate = restTemplate;
    }

    public AuthTokenResponse login(AuthLoginRequest request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", endpointFactory.clientId());
        form.add("client_secret", endpointFactory.clientSecret());
        form.add("username", request.username());
        form.add("password", request.password());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<AuthTokenResponse> response = restTemplate.exchange(
                    endpointFactory.realmTokenUrl(),
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
}

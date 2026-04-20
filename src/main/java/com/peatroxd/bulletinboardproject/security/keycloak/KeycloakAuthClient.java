package com.peatroxd.bulletinboardproject.security.keycloak;

import com.peatroxd.bulletinboardproject.auth.dto.request.AuthLoginRequest;
import com.peatroxd.bulletinboardproject.auth.dto.response.AuthTokenResponse;
import com.peatroxd.bulletinboardproject.common.exception.UnauthorizedException;
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
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());
        form.add("username", request.username());
        form.add("password", request.password());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<AuthTokenResponse> response = restTemplate.exchange(
                    realmTokenUrl(),
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
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    private String realmTokenUrl() {
        String base = properties.serverUrl().endsWith("/")
                ? properties.serverUrl().substring(0, properties.serverUrl().length() - 1)
                : properties.serverUrl();
        return base + "/realms/" + properties.realm() + "/protocol/openid-connect/token";
    }
}

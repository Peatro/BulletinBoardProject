package com.peatroxd.bulletinboardproject.security.keycloak;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class KeycloakAdminConfig {

    @Bean
    public Keycloak keycloakAdmin(KeycloakAdminProperties props) {
        String tokenRealm = StringUtils.hasText(props.tokenRealm()) ? props.tokenRealm() : props.realm();

        var builder = KeycloakBuilder.builder()
                .serverUrl(props.serverUrl())
                .realm(tokenRealm)
                .clientId(props.clientId());

        if (StringUtils.hasText(props.clientSecret())) {
            builder.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .clientSecret(props.clientSecret());
        } else {
            builder.grantType(OAuth2Constants.PASSWORD)
                    .username(props.username())
                    .password(props.password());
        }

        return builder.build();
    }
}

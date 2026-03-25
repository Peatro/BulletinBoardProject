package com.peatroxd.bulletinboardproject.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthTokenResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Long expiresIn,

        @JsonProperty("refresh_expires_in")
        Long refreshExpiresIn,

        @JsonProperty("scope")
        String scope
) {
}

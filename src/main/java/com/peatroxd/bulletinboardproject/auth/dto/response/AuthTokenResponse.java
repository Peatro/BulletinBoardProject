package com.peatroxd.bulletinboardproject.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Токены и метаданные сессии после успешного логина")
public record AuthTokenResponse(
        @JsonProperty("access_token")
        @Schema(description = "JWT access token", example = "eyJhbGciOi...")
        String accessToken,

        @JsonProperty("refresh_token")
        @Schema(description = "Refresh token", example = "eyJhbGciOi...")
        String refreshToken,

        @JsonProperty("token_type")
        @Schema(description = "Тип токена", example = "Bearer")
        String tokenType,

        @JsonProperty("expires_in")
        @Schema(description = "Время жизни access token в секундах", example = "300")
        Long expiresIn,

        @JsonProperty("refresh_expires_in")
        @Schema(description = "Время жизни refresh token в секундах", example = "1800")
        Long refreshExpiresIn,

        @JsonProperty("scope")
        @Schema(description = "OIDC scopes", example = "openid profile email")
        String scope
) {
}

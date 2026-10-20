package com.mrodriguezul.apptapp.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication result")
public record LoginResponseDto(
        @Schema(description = "Access token to send in the Authorization header", example = "eyJhbGciOiJIUzI1NiJ9...")
        String accessToken,
        @Schema(description = "Authorization scheme", example = "Bearer")
        String tokenType,
        @Schema(description = "Token lifetime in seconds", example = "3600")
        long expiresIn
) {
}

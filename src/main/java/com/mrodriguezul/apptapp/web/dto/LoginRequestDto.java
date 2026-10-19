package com.mrodriguezul.apptapp.web.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credentials used to obtain a JWT token")
public record LoginRequestDto(
        @Schema(description = "Username", example = "assistant", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String username,
        @Schema(description = "Password", example = "secret", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String password
) {
}

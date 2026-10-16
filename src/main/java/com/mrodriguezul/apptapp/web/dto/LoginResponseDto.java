package com.mrodriguezul.apptapp.web.dto;

public record LoginResponseDto(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}

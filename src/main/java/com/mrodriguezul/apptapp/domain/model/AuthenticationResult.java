package com.mrodriguezul.apptapp.domain.model;

public record AuthenticationResult(String token, long expiresIn) {
}

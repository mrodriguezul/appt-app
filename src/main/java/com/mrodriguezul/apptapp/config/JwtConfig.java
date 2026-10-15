package com.mrodriguezul.apptapp.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtConfig {
    private final String ISSUER = "mrodriguezul.com";
    private final String secretKey;
    //private final long EXPIRATION_TIME;

    public JwtConfig(@Value("${jwt.secret}") String secretKey) {
        if (secretKey == null || secretKey.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 256 bits");
        }
        this.secretKey = secretKey;
    }

    public Algorithm getJwtAlgorithm() {
        return Algorithm.HMAC256(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuer(ISSUER)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
                .sign(getJwtAlgorithm());
    }

    public boolean isValid(String jwt) {
        try {
            verify(jwt);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getUsername(String jwt) {
        return verify(jwt).getSubject();
    }

    private DecodedJWT verify(String jwt) {
        return JWT.require(getJwtAlgorithm())
                .withIssuer(ISSUER)
                .withClaimPresence("exp")
                .withClaimPresence("sub")
                .build()
                .verify(jwt);
    }
}

package com.mrodriguezul.apptapp.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtConfig {
    private final String issuer;
    private final Duration expiration;
    private final Algorithm algorithm;

    public JwtConfig(JwtProperties jwtProperties) {
        String secretKey = jwtProperties.secret();
        if (secretKey == null || secretKey.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 256 bits");
        }
        if(jwtProperties.issuer() == null || jwtProperties.issuer().isEmpty()) {
            throw new IllegalStateException("JWT_ISSUER must be set");
        }
        if(jwtProperties.expirationMinutes() <= 0){
            throw new IllegalStateException("JWT expiration must be greater than 0");
        }
        this.issuer = jwtProperties.issuer();
        this.expiration = Duration.ofMinutes(jwtProperties.expirationMinutes());
        this.algorithm = Algorithm.HMAC256(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuer(this.issuer)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(this.expiration)))
                .sign(this.algorithm);
    }

    public DecodedJWT verify(String token) {
        return JWT.require(this.algorithm)
                .withIssuer(this.issuer)
                .withClaimPresence("exp")
                .withClaimPresence("sub")
                .build()
                .verify(token);
    }

    public boolean isValid(String token) {
        try {
            verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return verify(token).getSubject();
    }

    public long getExpirationSeconds() {
        return expiration.toSeconds();
    }
}

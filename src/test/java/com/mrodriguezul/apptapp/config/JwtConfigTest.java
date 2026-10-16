package com.mrodriguezul.apptapp.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtConfigTest {
    private static final String SECRET = "01234567890123456789012345678901";
    private static final String ISSUER = "mrodriguezul.com";

    @Test
    void createToken_shouldGenerateVerifiableTokenWithUsername() {
        JwtConfig config = config();

        String token = config.createToken("alice");

        assertTrue(config.isValid(token));
        assertEquals("alice", config.getUsername(token));
        assertEquals(3600, config.getExpirationSeconds());
        assertEquals(ISSUER, config.verify(token).getIssuer());
        assertTrue(config.verify(token).getIssuedAt() != null);
        assertTrue(config.verify(token).getExpiresAt() != null);
    }

    @Test
    void verify_shouldRejectTokenWithoutRequiredClaims() {
        JwtConfig config = config();
        String token = com.auth0.jwt.JWT.create()
                .withIssuer(ISSUER)
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(SECRET));

        assertFalse(config.isValid(token));
        assertThrows(JWTVerificationException.class, () -> config.verify(token));
    }

    @Test
    void constructor_shouldRejectSecretShorterThan256Bits() {
        assertThrows(IllegalStateException.class,
                () -> new JwtConfig(new JwtProperties("short-secret", ISSUER, 60)));
    }

    @Test
    void constructor_shouldRejectMissingSecret() {
        assertThrows(IllegalStateException.class,
                () -> new JwtConfig(new JwtProperties(null, ISSUER, 60)));
    }

    @Test
    void constructor_shouldRejectMissingIssuer() {
        assertThrows(IllegalStateException.class,
                () -> new JwtConfig(new JwtProperties(SECRET, null, 60)));
    }

    @Test
    void constructor_shouldRejectBlankIssuer() {
        assertThrows(IllegalStateException.class,
                () -> new JwtConfig(new JwtProperties(SECRET, "", 60)));
    }

    @Test
    void constructor_shouldRejectNonPositiveExpiration() {
        assertThrows(IllegalStateException.class,
                () -> new JwtConfig(new JwtProperties(SECRET, ISSUER, 0)));
    }

    @Test
    void constructor_shouldRejectNegativeExpiration() {
        assertThrows(IllegalStateException.class,
                () -> new JwtConfig(new JwtProperties(SECRET, ISSUER, -1)));
    }

    private JwtConfig config() {
        return new JwtConfig(new JwtProperties(SECRET, ISSUER, 60));
    }
}

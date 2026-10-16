package com.mrodriguezul.apptapp.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtPropertiesTest {
    @Test
    void shouldExposeConfiguredValues() {
        JwtProperties properties = new JwtProperties("secret", "issuer", 60);

        assertEquals("secret", properties.secret());
        assertEquals("issuer", properties.issuer());
        assertEquals(60, properties.expirationMinutes());
    }
}

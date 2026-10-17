package com.mrodriguezul.apptapp.config;

import com.mrodriguezul.apptapp.config.security.RestAccessDeniedHandler;
import com.mrodriguezul.apptapp.config.security.RestAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SecurityConfigTest {
    @Test
    void passwordEncoder_shouldEncodeAndMatchPasswords() {
        SecurityConfig config = new SecurityConfig(mock(JwtFilter.class),
                mock(RestAuthenticationEntryPoint.class),
                mock(RestAccessDeniedHandler.class));

        PasswordEncoder encoder = config.passwordEncoder();
        String encoded = encoder.encode("password");

        assertNotEquals("password", encoded);
        assertTrue(encoder.matches("password", encoded));
    }

    @Test
    void authenticationManager_shouldDelegateToAuthenticationConfiguration() throws Exception {
        AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
        AuthenticationManager expected = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(expected);
        SecurityConfig config = new SecurityConfig(mock(JwtFilter.class),
                mock(RestAuthenticationEntryPoint.class),
                mock(RestAccessDeniedHandler.class));

        assertSame(expected, config.authenticationManager(authenticationConfiguration));
        verify(authenticationConfiguration).getAuthenticationManager();
    }
}

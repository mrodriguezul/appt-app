package com.mrodriguezul.apptapp.domain.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditUsernameServiceTest {
    private final AuditUsernameService service = new AuditUsernameService();

    @Mock
    private org.springframework.security.core.Authentication authentication;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentAuditor_shouldReturnAuthenticatedUsername() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("alice");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertEquals(java.util.Optional.of("alice"), service.getCurrentAuditor());
    }

    @Test
    void getCurrentAuditor_shouldIgnoreAnonymousAuthentication() {
        AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken(
                "key", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        assertTrue(service.getCurrentAuditor().isEmpty());
    }

    @Test
    void getCurrentAuditor_shouldIgnoreUnauthenticatedAuthentication() {
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertTrue(service.getCurrentAuditor().isEmpty());
    }

    @Test
    void getCurrentAuditor_shouldReturnEmptyWhenContextHasNoAuthentication() {
        assertTrue(service.getCurrentAuditor().isEmpty());
    }
}

package com.mrodriguezul.apptapp.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {
    @Mock
    private JwtConfig jwtConfig;
    @Mock
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @Mock
    private FilterChain filterChain;
    @Mock
    private DecodedJWT decodedJWT;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validToken_shouldAuthenticateUserAndContinueFilterChain() throws Exception {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("alice")
                .password("encoded")
                .roles("ASSISTANT")
                .build();
        when(jwtConfig.isValid("valid-token")).thenReturn(true);
        when(jwtConfig.verify("valid-token")).thenReturn(decodedJWT);
        when(decodedJWT.getSubject()).thenReturn("alice");
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        new JwtFilter(jwtConfig, userDetailsService)
                .doFilter(request, response, filterChain);

        assertEquals("alice", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(eq(request), eq(response));
    }

    @Test
    void invalidToken_shouldNotAuthenticateAndContinueFilterChain() throws Exception {
        when(jwtConfig.isValid("invalid-token")).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        new JwtFilter(jwtConfig, userDetailsService).doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtConfig, never()).verify("invalid-token");
    }

    @Test
    void requestWithoutBearerToken_shouldContinueFilterChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        new JwtFilter(jwtConfig, userDetailsService).doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtConfig, userDetailsService);
    }
}

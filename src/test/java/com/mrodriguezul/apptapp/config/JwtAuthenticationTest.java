package com.mrodriguezul.apptapp.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mrodriguezul.apptapp.domain.model.AuthenticationResult;
import com.mrodriguezul.apptapp.domain.service.UserSecurityService;
import com.mrodriguezul.apptapp.persistence.adapter.UserRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthenticationTest {
    private static final String SECRET = "01234567890123456789012345678901";
    private static final String ISSUER = "mrodriguezul.com";

    private JwtConfig jwtConfig() {
        return new JwtConfig(new JwtProperties(SECRET, ISSUER, 60));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesCorrectCredentialsAndCreatesToken() {
        AuthenticationManager manager = mock(AuthenticationManager.class);
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("alice").password("encoded").roles("ADMIN").build();
        when(manager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        UserSecurityService service = new UserSecurityService(manager, jwtConfig(), mock(UserRepository.class));

        AuthenticationResult authResult = service.loginUser("alice", "password");
        String token = authResult.token();

        assertEquals("alice", jwtConfig().getUsername(token));
        verify(manager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void rejectsIncorrectCredentials() {
        AuthenticationManager manager = mock(AuthenticationManager.class);
        when(manager.authenticate(any())).thenThrow(new BadCredentialsException("invalid"));
        UserSecurityService service = new UserSecurityService(manager, jwtConfig(), mock(UserRepository.class));

        assertThrows(BadCredentialsException.class, () -> service.loginUser("alice", "wrong"));
    }

    @Test
    void rejectsInvalidTokenSignature() {
        JwtConfig config = jwtConfig();
        String token = JWT.create().withSubject("alice").withIssuer(ISSUER)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(60)))
                .sign(Algorithm.HMAC256("11111111111111111111111111111111"));

        assertFalse(config.isValid(token));
    }

    @Test
    void rejectsExpiredToken() {
        JwtConfig config = jwtConfig();
        String token = JWT.create().withSubject("alice").withIssuer(ISSUER)
                .withExpiresAt(Date.from(Instant.now().minusSeconds(60)))
                .sign(Algorithm.HMAC256(SECRET));

        assertFalse(config.isValid(token));
    }

    @Test
    void doesNotAuthenticateDisabledUserFromToken() throws Exception {
        JwtConfig config = jwtConfig();
        UserDetails disabled = org.springframework.security.core.userdetails.User
                .withUsername("alice").password("encoded").roles("ADMIN").disabled(true).build();
        UserDetailsServiceStub userDetailsService = new UserDetailsServiceStub(disabled);
        JwtFilter filter = new JwtFilter(config, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + config.createToken("alice"));

        filter.doFilter(request, new MockHttpServletResponse(), mock(FilterChain.class));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void authenticatesEnabledUserWithUserDetailsAndNullCredentials() throws Exception {
        JwtConfig config = jwtConfig();
        UserDetails enabled = org.springframework.security.core.userdetails.User
                .withUsername("alice").password("encoded").roles("ASSISTANT").build();
        JwtFilter filter = new JwtFilter(config, new UserDetailsServiceStub(enabled));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + config.createToken("alice"));

        filter.doFilter(request, new MockHttpServletResponse(), mock(FilterChain.class));

        assertInstanceOf(UserDetails.class, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertNull(SecurityContextHolder.getContext().getAuthentication().getCredentials());
    }

    private static final class UserDetailsServiceStub
            implements org.springframework.security.core.userdetails.UserDetailsService {
        private final UserDetails userDetails;

        private UserDetailsServiceStub(UserDetails userDetails) {
            this.userDetails = userDetails;
        }

        @Override
        public UserDetails loadUserByUsername(String username) {
            return userDetails;
        }
    }
}

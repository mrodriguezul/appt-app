package com.mrodriguezul.apptapp.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestAuthenticationEntryPointTest {
    @Mock
    private SecurityErrorResponseWriter errorResponseWriter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Test
    void commence_shouldWriteUnauthorizedResponse() throws Exception {
        new RestAuthenticationEntryPoint(errorResponseWriter)
                .commence(request, response, new BadCredentialsException("invalid credentials"));

        verify(errorResponseWriter).writeErrorResponse(
                eq(response),
                eq(HttpStatus.UNAUTHORIZED),
                eq("Authentication required: invalid credentials"));
    }
}

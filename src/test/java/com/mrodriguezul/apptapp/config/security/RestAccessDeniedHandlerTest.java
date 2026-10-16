package com.mrodriguezul.apptapp.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestAccessDeniedHandlerTest {
    @Mock
    private SecurityErrorResponseWriter errorResponseWriter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Test
    void handle_shouldWriteForbiddenResponse() throws Exception {
        new RestAccessDeniedHandler(errorResponseWriter)
                .handle(request, response, new AccessDeniedException("insufficient role"));

        verify(errorResponseWriter).writeErrorResponse(
                eq(response),
                eq(HttpStatus.FORBIDDEN),
                eq("Access denied: insufficient role"));
    }
}

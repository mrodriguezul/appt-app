package com.mrodriguezul.apptapp.config.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class SecurityErrorResponseWriterTest {
    @Test
    void writeErrorResponse_shouldWriteJsonErrorWithStatusAndTimestamp() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new SecurityErrorResponseWriter(new ObjectMapper())
                .writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authentication required: missing token");

        JsonNode body = new ObjectMapper().readTree(response.getContentAsString());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertTrue(response.getContentType().startsWith("application/json"));
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertEquals("Unauthorized", body.get("error").asText());
        assertEquals("Authentication required: missing token", body.get("message").asText());
        assertEquals(401, body.get("status").asInt());
        assertTrue(body.get("timestamp").asLong() > 0);
    }
}

package com.mrodriguezul.apptapp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrodriguezul.apptapp.domain.model.AuthenticationResult;
import com.mrodriguezul.apptapp.domain.service.UserSecurityService;
import com.mrodriguezul.apptapp.web.dto.LoginRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private UserSecurityService userSecurityService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(userSecurityService)).build();
    }

    @Test
    void login_shouldReturnBearerTokenAndExpiration() throws Exception {
        when(userSecurityService.loginUser("alice", "password"))
                .thenReturn(new AuthenticationResult("jwt-token", 3600));

        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequestDto("alice", "password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));

        verify(userSecurityService).loginUser(eq("alice"), eq("password"));
    }
}

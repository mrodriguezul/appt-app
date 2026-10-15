package com.mrodriguezul.apptapp.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrodriguezul.apptapp.config.security.RestAccessDeniedHandler;
import com.mrodriguezul.apptapp.config.security.RestAuthenticationEntryPoint;
import com.mrodriguezul.apptapp.config.security.SecurityErrorResponseWriter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.Instant;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SecurityAuthorizationTest.TestBeans.class)
@WebAppConfiguration
@AutoConfigureMockMvc
class SecurityAuthorizationTest {
    private static final String JWT_SECRET = "01234567890123456789012345678901";
    private static final String JWT_ISSUER = "mrodriguezul.com";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicDoctorEndpointsAreAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/doctor")).andExpect(status().isOk());
        mockMvc.perform(get("/doctor/specialty/1")).andExpect(status().isOk());
        mockMvc.perform(get("/doctor/search/name")).andExpect(status().isOk());
    }

    @Test
    void protectedEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/patient")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/appointment")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/doctor/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void invalidOrExpiredJwtCannotAccessProtectedEndpoint() throws Exception {
        String expiredToken = JWT.create()
                .withSubject("assistant")
                .withIssuer(JWT_ISSUER)
                .withExpiresAt(Date.from(Instant.now().minusSeconds(60)))
                .sign(Algorithm.HMAC256(JWT_SECRET));

        mockMvc.perform(get("/patient").header(AUTHORIZATION, "Bearer invalid"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/patient").header(AUTHORIZATION, "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ASSISTANT")
    void assistantCanUsePatientAppointmentAndDoctorReadEndpoints() throws Exception {
        mockMvc.perform(get("/patient")).andExpect(status().isOk());
        mockMvc.perform(get("/patient/search")).andExpect(status().isOk());
        mockMvc.perform(get("/patient/by-identification/01/123")).andExpect(status().isOk());
        mockMvc.perform(post("/patient")).andExpect(status().isOk());
        mockMvc.perform(put("/patient/1")).andExpect(status().isOk());
        mockMvc.perform(get("/appointment")).andExpect(status().isOk());
        mockMvc.perform(get("/appointment/1")).andExpect(status().isOk());
        mockMvc.perform(post("/appointment")).andExpect(status().isOk());
        mockMvc.perform(put("/appointment/1")).andExpect(status().isOk());
        mockMvc.perform(get("/doctor/identification/123")).andExpect(status().isOk());
        mockMvc.perform(get("/doctor/identification/01/123")).andExpect(status().isOk());
        mockMvc.perform(get("/doctor/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ASSISTANT")
    void assistantCannotModifyDoctors() throws Exception {
        mockMvc.perform(post("/doctor")).andExpect(status().isForbidden());
        mockMvc.perform(put("/doctor")).andExpect(status().isForbidden());
        mockMvc.perform(delete("/doctor/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanUseAssistantAndDoctorAdministrationEndpoints() throws Exception {
        mockMvc.perform(get("/patient")).andExpect(status().isOk());
        mockMvc.perform(get("/appointment")).andExpect(status().isOk());
        mockMvc.perform(get("/doctor/1")).andExpect(status().isOk());
        mockMvc.perform(post("/doctor")).andExpect(status().isOk());
        mockMvc.perform(put("/doctor")).andExpect(status().isOk());
        mockMvc.perform(delete("/doctor/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotUseAssistantEndpoints() throws Exception {
        mockMvc.perform(get("/patient")).andExpect(status().isForbidden());
        mockMvc.perform(get("/appointment")).andExpect(status().isForbidden());
        mockMvc.perform(get("/doctor/1")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ASSISTANT")
    void undeclaredRoutesAreDenied() throws Exception {
        mockMvc.perform(get("/admin/users")).andExpect(status().isForbidden());
    }

    @RestController
    static class AuthorizationProbeController {
        @GetMapping("/doctor")
        void getDoctors() {}

        @GetMapping("/doctor/specialty/{id}")
        void getDoctorsBySpecialty() {}

        @GetMapping("/doctor/search/name")
        void searchDoctors() {}

        @GetMapping("/doctor/identification/{value}")
        void getDoctorByIdentification() {}

        @GetMapping("/doctor/identification/{type}/{number}")
        void getDoctorByTypedIdentification() {}

        @GetMapping("/doctor/{id}")
        void getDoctor() {}

        @PostMapping("/doctor")
        void createDoctor() {}

        @PutMapping("/doctor")
        void updateDoctor() {}

        @DeleteMapping("/doctor/{id}")
        void deleteDoctor() {}

        @GetMapping("/patient")
        void getPatients() {}

        @GetMapping("/patient/search")
        void searchPatients() {}

        @GetMapping("/patient/by-identification/{type}/{number}")
        void getPatientByIdentification() {}

        @PostMapping("/patient")
        void createPatient() {}

        @PutMapping("/patient/{id}")
        void updatePatient() {}

        @GetMapping("/appointment")
        void getAppointments() {}

        @GetMapping("/appointment/{id}")
        void getAppointment() {}

        @PostMapping("/appointment")
        void createAppointment() {}

        @PutMapping("/appointment/{id}")
        void updateAppointment() {}
    }

    @TestConfiguration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, RestAuthenticationEntryPoint.class, RestAccessDeniedHandler.class, SecurityErrorResponseWriter.class})
    static class TestBeans {
        @Bean
        AuthorizationProbeController authorizationProbeController() {
            return new AuthorizationProbeController();
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        JwtFilter jwtFilter() {
            return new JwtFilter(new JwtConfig(new JwtProperties(JWT_SECRET, JWT_ISSUER, 60)),
                    mock(UserDetailsService.class));
        }

    }
}

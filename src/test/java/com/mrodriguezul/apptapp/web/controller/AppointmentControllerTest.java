package com.mrodriguezul.apptapp.web.controller;

import com.mrodriguezul.apptapp.domain.model.Appointment;
import com.mrodriguezul.apptapp.domain.service.AppointmentService;
import com.mrodriguezul.apptapp.web.dto.AppointmentRequestDto;
import com.mrodriguezul.apptapp.web.dto.AppointmentResponseDto;
import com.mrodriguezul.apptapp.web.mapper.AppointmentWebMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private AppointmentWebMapper appointmentWebMapper;
    @InjectMocks
    private AppointmentController appointmentController;

    @Test
    void createDelegatesRequestAndResponseConversionsToWebMapper() {
        AppointmentRequestDto request = new AppointmentRequestDto(
                1L, 2L, LocalDateTime.now().plusHours(1), "Consulta");
        Appointment appointment = new Appointment();
        AppointmentResponseDto response = new AppointmentResponseDto(
                10L, 1L, 2L, request.appointmentDate(), request.reason());

        when(appointmentWebMapper.toModel(request)).thenReturn(appointment);
        when(appointmentService.create(appointment)).thenReturn(appointment);
        when(appointmentWebMapper.toResponse(appointment)).thenReturn(response);

        ResponseEntity<AppointmentResponseDto> result = appointmentController.create(request);

        assertEquals(201, result.getStatusCode().value());
        assertEquals(response, result.getBody());
        verify(appointmentWebMapper).toModel(request);
        verify(appointmentWebMapper).toResponse(appointment);
    }

    @Test
    void findByIdDelegatesResponseConversionToWebMapper() {
        Appointment appointment = new Appointment();
        AppointmentResponseDto response = new AppointmentResponseDto(
                10L, 1L, 2L, LocalDateTime.now().plusHours(1), "Consulta");
        when(appointmentService.findById(10L)).thenReturn(java.util.Optional.of(appointment));
        when(appointmentWebMapper.toResponse(appointment)).thenReturn(response);

        ResponseEntity<AppointmentResponseDto> result = appointmentController.findById(10L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
        verify(appointmentWebMapper).toResponse(appointment);
    }
}

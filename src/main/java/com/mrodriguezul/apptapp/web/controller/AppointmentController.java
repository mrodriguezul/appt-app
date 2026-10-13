package com.mrodriguezul.apptapp.web.controller;

import com.mrodriguezul.apptapp.domain.model.Appointment;
import com.mrodriguezul.apptapp.domain.service.AppointmentService;
import com.mrodriguezul.apptapp.web.dto.AppointmentRequestDto;
import com.mrodriguezul.apptapp.web.dto.AppointmentResponseDto;
import com.mrodriguezul.apptapp.web.mapper.AppointmentWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final AppointmentWebMapper appointmentWebMapper;

    public AppointmentController(AppointmentService appointmentService, AppointmentWebMapper appointmentWebMapper) {
        this.appointmentService = appointmentService;
        this.appointmentWebMapper = appointmentWebMapper;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDto> create(@RequestBody @Valid AppointmentRequestDto request) {
        Appointment appointment = appointmentService.create(appointmentWebMapper.toModel(request));
        return ResponseEntity.status(201).body(appointmentWebMapper.toResponse(appointment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> update(@PathVariable Long id, @RequestBody @Valid AppointmentRequestDto request) {
        Appointment appointment = appointmentService.update(id, appointmentWebMapper.toModel(request));
        return ResponseEntity.ok(appointmentWebMapper.toResponse(appointment));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDto>> findAll() {
        return ResponseEntity.ok(appointmentService.findAll().stream()
                .map(appointmentWebMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> findById(@PathVariable Long id) {
        return appointmentService.findById(id)
                .map(appointment -> ResponseEntity.ok(appointmentWebMapper.toResponse(appointment)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

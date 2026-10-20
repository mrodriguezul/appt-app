package com.mrodriguezul.apptapp.web.controller;

import com.mrodriguezul.apptapp.domain.model.Appointment;
import com.mrodriguezul.apptapp.domain.service.AppointmentService;
import com.mrodriguezul.apptapp.web.dto.AppointmentRequestDto;
import com.mrodriguezul.apptapp.web.dto.AppointmentResponseDto;
import com.mrodriguezul.apptapp.web.mapper.AppointmentWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Appointment", description = "Appointment management for ASSISTANT and ADMIN users")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final AppointmentWebMapper appointmentWebMapper;

    public AppointmentController(AppointmentService appointmentService, AppointmentWebMapper appointmentWebMapper) {
        this.appointmentService = appointmentService;
        this.appointmentWebMapper = appointmentWebMapper;
    }

    @PostMapping
    @Operation(summary = "Create an appointment",
            description = "Creates an appointment for an existing doctor and patient.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = AppointmentRequestDto.class))))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Appointment created", content = @Content(schema = @Schema(implementation = AppointmentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid appointment data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not ASSISTANT or ADMIN"),
            @ApiResponse(responseCode = "404", description = "Doctor or patient not found"),
            @ApiResponse(responseCode = "409", description = "Doctor already has an appointment at that time")
    })
    public ResponseEntity<AppointmentResponseDto> create(@RequestBody @Valid AppointmentRequestDto request) {
        Appointment appointment = appointmentService.create(appointmentWebMapper.toModel(request));
        return ResponseEntity.status(201).body(appointmentWebMapper.toResponse(appointment));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an appointment",
            description = "Updates an existing appointment using a future date with minute precision.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = AppointmentRequestDto.class))))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment updated", content = @Content(schema = @Schema(implementation = AppointmentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid appointment data or identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not ASSISTANT or ADMIN"),
            @ApiResponse(responseCode = "404", description = "Appointment, doctor or patient not found"),
            @ApiResponse(responseCode = "409", description = "Doctor already has an appointment at that time")
    })
    @Parameter(name = "id", description = "Appointment identifier", required = true, example = "1")
    public ResponseEntity<AppointmentResponseDto> update(@PathVariable Long id, @RequestBody @Valid AppointmentRequestDto request) {
        Appointment appointment = appointmentService.update(id, appointmentWebMapper.toModel(request));
        return ResponseEntity.ok(appointmentWebMapper.toResponse(appointment));
    }

    @GetMapping
    @Operation(summary = "List appointments", description = "Returns all appointments.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments returned"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not ASSISTANT or ADMIN")
    })
    public ResponseEntity<List<AppointmentResponseDto>> findAll() {
        return ResponseEntity.ok(appointmentService.findAll().stream()
                .map(appointmentWebMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an appointment", description = "Returns an appointment by identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment found", content = @Content(schema = @Schema(implementation = AppointmentResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid appointment identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not ASSISTANT or ADMIN"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @Parameter(name = "id", description = "Appointment identifier", required = true, example = "1")
    public ResponseEntity<AppointmentResponseDto> findById(@PathVariable Long id) {
        return appointmentService.findById(id)
                .map(appointment -> ResponseEntity.ok(appointmentWebMapper.toResponse(appointment)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

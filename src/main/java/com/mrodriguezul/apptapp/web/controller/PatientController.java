package com.mrodriguezul.apptapp.web.controller;

import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.service.PatientService;
import com.mrodriguezul.apptapp.web.dto.PatientRequestDto;
import com.mrodriguezul.apptapp.web.dto.PatientResponseDto;
import com.mrodriguezul.apptapp.web.mapper.PatientWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Patient", description = "Operations - patients 👴🧓")
@RestController
@RequestMapping("/patient")
@SecurityRequirement(name = "bearerAuth")
public class PatientController {
    private final PatientService patientService;
    private final PatientWebMapper patientWebMapper;

    public PatientController(PatientService patientService, PatientWebMapper patientWebMapper) {
        this.patientService = patientService;
        this.patientWebMapper = patientWebMapper;
    }

    @Operation(summary = "List patients",
            description = "Returns a paginated list. An empty page is returned when no patients match.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not ASSISTANT or ADMIN")
    })
    @Parameters(value = {
            @Parameter(name = "page", description = "Page number", example = "0"),
            @Parameter(name = "size", description = "Number of page elements", example = "10")
    })
    @GetMapping
    public Page<PatientResponseDto> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return patientService.getAll(page, size).map(patientWebMapper::toResponse);
    }

    @Operation(summary = "Search patients",
            description = "Returns a paginated list filtered by names and/or surnames.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients found"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameter"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not ASSISTANT or ADMIN")
    })
    @Parameters(value = {
            @Parameter(name = "names", description = "Names filter", example = "Ana"),
            @Parameter(name = "surnames", description = "Surnames filter", example = "Pérez"),
            @Parameter(name = "page", description = "Page number", example = "0"),
            @Parameter(name = "size", description = "Number of page elements", example = "10"),
            @Parameter(name = "sortBy", description = "Sort field", example = "id"),
            @Parameter(name = "sortDir", description = "Directorate of ordering (ASC/DESC)", example = "ASC")
    })
    @GetMapping("/search")
    public Page<PatientResponseDto> getAllBySearchNamesOrSurnames(
            @RequestParam(defaultValue = "") String names,
            @RequestParam(defaultValue = "") String surnames,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        return patientService.getAllByNameOrSurname(names, surnames, page, size, sortBy, sortDir).map(patientWebMapper::toResponse);
    }

    @GetMapping("/by-identification/{documentTypeCode}/{documentNumber}")
    @Operation(summary = "Find a patient by identification",
            description = "Returns the patient matching the document type code and number.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient found", content = @Content(schema = @Schema(implementation = PatientResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid document type code"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not ASSISTANT or ADMIN"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @Parameters({
            @Parameter(name = "documentTypeCode", description = "Document type code", required = true, example = "01", in = ParameterIn.PATH),
            @Parameter(name = "documentNumber", description = "Document number", required = true, example = "12345678", in = ParameterIn.PATH)
    })
    public ResponseEntity<PatientResponseDto> getPatientByIdentification(
            @PathVariable String documentTypeCode,
            @PathVariable String documentNumber) {
        return ResponseEntity.ok(patientWebMapper.toResponse(patientService.getByDocumentTypeAndDocumentNumber(DocumentType.fromCode(documentTypeCode), documentNumber)));
    }

    @PostMapping
    @Operation(summary = "Create a patient",
            description = "Creates a new patient without an identifier.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = PatientRequestDto.class))))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Patient created", content = @Content(schema = @Schema(implementation = PatientResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid patient data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not ASSISTANT or ADMIN"),
            @ApiResponse(responseCode = "409", description = "Patient or personal identification already exists")
    })
    public ResponseEntity<PatientResponseDto> create(@Valid @RequestBody PatientRequestDto request) {
        Patient patient = patientService.create( patientWebMapper.toModel(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(patientWebMapper.toResponse(patient));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a patient",
            description = "Updates a patient while preserving its identification.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = PatientRequestDto.class))))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient updated", content = @Content(schema = @Schema(implementation = PatientResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid patient data or identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not ASSISTANT or ADMIN"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "409", description = "Patient identification cannot be changed")
    })
    @Parameter(name = "id", description = "Patient identifier", required = true, example = "1")
    public ResponseEntity<PatientResponseDto> update(@PathVariable Long id, @Valid @RequestBody PatientRequestDto request) {
        Patient patient = patientService.update(id, patientWebMapper.toModel(request));
        return ResponseEntity.ok(patientWebMapper.toResponse(patient));
    }

}

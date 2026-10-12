package com.mrodriguezul.apptapp.web.controller;

import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.service.PatientService;
import com.mrodriguezul.apptapp.web.dto.PatientRequestDto;
import com.mrodriguezul.apptapp.web.dto.PatientResponseDto;
import com.mrodriguezul.apptapp.web.mapper.PatientWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Patient", description = "Operations - patients 👴🧓")
@RestController
@RequestMapping("/patient")
public class PatientController {
    private final PatientService patientService;
    private final PatientWebMapper patientWebMapper;

    public PatientController(PatientService patientService, PatientWebMapper patientWebMapper) {
        this.patientService = patientService;
        this.patientWebMapper = patientWebMapper;
    }

    @Operation(summary = "Get all patients", description = "Returns a paginated list of patients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients found"),
            @ApiResponse(responseCode = "404", description = "No patients were found")
    })
    @Parameters(value = {
            @Parameter(name = "page", description = "Page number", example = "0"),
            @Parameter(name = "size", description = "Number of page elements", example = "10")
    })
    @GetMapping
    public Page<PatientResponseDto> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return patientService.getAll(page, size).map(patientWebMapper::toResponse);
    }

    @Operation(summary = "Obtain patients by identification", description = "Returns a paginated list of patients filtered by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients found"),
            @ApiResponse(responseCode = "404", description = "No patients were found")
    })
    @Parameters(value = {
            @Parameter(name = "personIdentificationId", description = "ID de la identificación de la persona", required = true, example = "1", in = ParameterIn.PATH),
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
    public ResponseEntity<PatientResponseDto> getPatientByIdentification(
            @PathVariable String documentTypeCode,
            @PathVariable String documentNumber) {
        return ResponseEntity.ok(patientWebMapper.toResponse(patientService.getByDocumentTypeAndDocumentNumber(DocumentType.fromCode(documentTypeCode), documentNumber)));
    }

    @PostMapping
    public ResponseEntity<PatientResponseDto> create(@Valid @RequestBody PatientRequestDto request) {
        Patient patient = patientService.create( patientWebMapper.toModel(request));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(patientWebMapper.toResponse(patient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> update(@PathVariable Long id, @Valid @RequestBody PatientRequestDto request) {
        Patient patient = patientService.update(id, patientWebMapper.toModel(request));
        return ResponseEntity.ok(patientWebMapper.toResponse(patient));
    }

}

package com.mrodriguezul.apptapp.web.controller;

import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Patient", description = "Operations - patients 👴🧓")
@RestController
@RequestMapping("/paciente")
public class PatientController {
    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(summary = "Get all patients", description = "Returns a paginated list of patients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients found"),
            @ApiResponse(responseCode = "404", description = "No patients were found")
    })
    @Parameters(value = {
            @Parameter(name = "page", description = "Page number", required = false, example = "0"),
            @Parameter(name = "size", description = "Number of page elements", required = false, example = "10")
    })
    @GetMapping
    public Page<Patient> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return patientService.getAll(page, size);
    }

    @Operation(summary = "Obtain patients by identification", description = "Returns a paginated list of patients filtered by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients found"),
            @ApiResponse(responseCode = "404", description = "No patients were found")
    })
    @Parameters(value = {
            @Parameter(name = "personIdentificationId", description = "ID de la identificación de la persona", required = true, example = "1", in = ParameterIn.PATH),
            @Parameter(name = "page", description = "Page number", required = false, example = "0"),
            @Parameter(name = "size", description = "Number of page elements", required = false, example = "10"),
            @Parameter(name = "sortBy", description = "Sort field", required = false, example = "id"),
            @Parameter(name = "sortDir", description = "Directorate of ordering (ASC/DESC)", required = false, example = "ASC")
    })
    @GetMapping("/by-identification/{documentTypeCode}/{documentNumber}")
    public Page<Patient> getAllByIdentificationId(
            @PathVariable String documentTypeCode,
            @PathVariable String documentNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        return patientService.getAllByDocumentTypeAndDocumentNumber(DocumentType.fromCode(documentTypeCode), documentNumber, page, size, sortBy, sortDir);
    }

}

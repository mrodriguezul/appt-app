package com.mrodriguezul.apptapp.web.controller;


import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.service.DoctorService;
import com.mrodriguezul.apptapp.web.dto.DoctorRequestDto;
import com.mrodriguezul.apptapp.web.dto.DoctorResponseDto;
import com.mrodriguezul.apptapp.web.mapper.DoctorWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Doctor", description = "Public doctor searches; detailed and administrative operations require ASSISTANT or ADMIN")
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorWebMapper doctorWebMapper;

    @Autowired
    public DoctorController(DoctorService doctorService, DoctorWebMapper doctorWebMapper) {
        this.doctorService = doctorService;
        this.doctorWebMapper = doctorWebMapper;
    }

    @Operation(summary = "List doctors",
            description = "Public operation that returns all doctors.")
    @ApiResponse(responseCode = "200", description = "Doctors returned")
    @GetMapping
    public List<DoctorResponseDto> getAll() {
        return doctorService.getAll().stream()
                .map(doctorWebMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Search doctors by name",
            description = "Public operation that returns doctors matching the optional name or surname filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors found"),
            @ApiResponse(responseCode = "404", description = "Doctors not found")
    })
    @Parameters(value = {
            @Parameter(name = "names", description = "Doctor's name", required = false, example = "Juan Carlos"),
            @Parameter(name = "surnames", description = "Doctor's surname", required = false, example = "García López")
    })
    @GetMapping("/search/name")
    public ResponseEntity<List<DoctorResponseDto>> getDoctorsByNameOrSurname(
            @RequestParam(required = false) String names,
            @RequestParam(required = false) String surnames) {
        List<Doctor> doctors = doctorService.getAllByNameOrSurname(names, surnames);
        if (doctors.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doctors.stream()
                .map(doctorWebMapper::toResponse)
                .toList());
    }

    @Operation(summary = "Search doctors by specialty",
            description = "Public operation that returns doctors assigned to a specialty.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors found"),
            @ApiResponse(responseCode = "404", description = "No doctors could be found for the specialty")
    })
    @Parameters(value = {
            @Parameter(name = "specialityId", description = "ID of the specialty to search for", required = true, example = "1")
    })
    @GetMapping("/specialty/{specialityId}")
    public ResponseEntity<List<DoctorResponseDto>> getDoctorsBySpeciality(@PathVariable Long specialityId) {
        List<Doctor> doctors = doctorService.getAllBySpeciality(specialityId);
        if (doctors.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doctors.stream()
                .map(doctorWebMapper::toResponse)
                .toList());
    }

    @Operation(summary = "Get a doctor by ID",
            description = "Returns a doctor by identifier. Requires ASSISTANT or ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "400", description = "Invalid doctor identifier"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not ASSISTANT or ADMIN")
    })
    @Parameters(value = {
            @Parameter(name = "id", description = "ID of the doctor to search for", required = true, example = "1")
    })
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DoctorResponseDto> getDoctor(@PathVariable Long id) {
        return doctorService.getDoctor(id)
                .map(doctorWebMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get a doctor by document number",
            description = "Returns a doctor by document number. Requires ASSISTANT or ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not ASSISTANT or ADMIN")
    })
    @Parameters(value = {
            @Parameter(name = "identificationNumber", description = "Doctor's identification number", required = true, example = "12345678")
    })
    @GetMapping("/identification/{identificationNumber}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DoctorResponseDto> getDoctorByIdentificationNumber(@PathVariable String identificationNumber) {
        return doctorService.getAllByDocumentNumber(identificationNumber)
                .map(doctorWebMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get a doctor by document type and number",
            description = "Returns a doctor by document type code and number. Requires ASSISTANT or ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "400", description = "Invalid document type code"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User is not ASSISTANT or ADMIN")
    })
    @Parameters(value = {
            @Parameter(name = "documentTypeCode", description = "Code of the identification type", required = true, example = "01"),
            @Parameter(name = "documentNumber", description = "Doctor's identification number", required = true, example = "12345678")
    })
    @GetMapping("/identification/{documentTypeCode}/{documentNumber}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DoctorResponseDto> getDoctorByIdentificationTypeAndNumber(
            @PathVariable String documentTypeCode,
            @PathVariable String documentNumber) {
        return doctorService.getDoctorByDocumentTypeAndDocumentNumber(DocumentType.fromCode(documentTypeCode), documentNumber)
                .map(doctorWebMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a doctor",
            description = "Admin-only operation that creates a doctor when the identifier is new.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(schema = @Schema(implementation = DoctorRequestDto.class))))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Doctor successfully created"),
        @ApiResponse(responseCode = "400", description = "Doctor already exists or request is invalid"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DoctorResponseDto> create(@Valid @RequestBody DoctorRequestDto request) {
        Doctor doctor = doctorService.create(doctorWebMapper.toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorWebMapper.toResponse(doctor));
    }

    @Operation(summary = "Update a doctor",
            description = "Admin-only operation that updates an existing doctor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor successfully updated"),
            @ApiResponse(responseCode = "400", description = "Doctor identifier is missing or does not exist"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DoctorResponseDto> update(@Valid @RequestBody DoctorRequestDto request) {
        Doctor doctor = doctorWebMapper.toModel(request);
        Doctor updatedDoctor = doctorService.update(doctor.getId(), doctor);
        return ResponseEntity.ok(doctorWebMapper.toResponse(updatedDoctor));
    }

    @Operation(summary = "Delete a doctor",
            description = "Admin-only operation that deletes a doctor by identifier.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Doctor successfully removed"),
        @ApiResponse(responseCode = "404", description = "Doctor not found"),
        @ApiResponse(responseCode = "400", description = "Invalid doctor identifier"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "ADMIN role required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

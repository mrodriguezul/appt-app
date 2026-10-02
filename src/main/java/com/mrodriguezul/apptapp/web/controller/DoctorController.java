package com.mrodriguezul.apptapp.web.controller;


import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Doctor", description = "Operations - doctors 👨‍🔬")
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Operation(summary = "Get all the doctors", description = "Return a list of all the doctors")
    @GetMapping
    public List<Doctor> getAll() {
        return doctorService.getAll();
    }

    @Operation(summary = "Search for doctors by first or last name", description = "Returns a list of doctors that match the provided first or last name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors found"),
            @ApiResponse(responseCode = "404", description = "Doctors not found")
    })
    @Parameters(value = {
            @Parameter(name = "names", description = "Doctor's name", required = false, example = "Juan Carlos"),
            @Parameter(name = "surnames", description = "Doctor's surname", required = false, example = "García López")
    })
    @GetMapping("/search/name")
    public ResponseEntity<List<Doctor>> getDoctorsByNameOrSurname(
            @RequestParam(required = false) String names,
            @RequestParam(required = false) String surnames) {
        List<Doctor> doctors = doctorService.getAllByNameOrSurname(names, surnames);
        if (doctors.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doctors);
    }

    @Operation(summary = "Search for doctors by specialty", description = "Returns a list of doctors who belong to a specific specialty")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors found"),
            @ApiResponse(responseCode = "404", description = "No doctors could be found for the specialty")
    })
    @Parameters(value = {
            @Parameter(name = "specialityId", description = "ID of the specialty to search for", required = true, example = "1")
    })
    @GetMapping("/specialty/{specialityId}")
    public ResponseEntity<List<Doctor>> getDoctorsBySpeciality(@PathVariable Long specialityId) {
        List<Doctor> doctors = doctorService.getAllBySpeciality(specialityId);
        if (doctors.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doctors);
    }

    @Operation(summary = "Get a doctor by ID", description = "Return a specific doctor by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @Parameters(value = {
            @Parameter(name = "id", description = "ID of the doctor to search for", required = true, example = "1")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id) {
        return doctorService.getDoctor(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Search for a doctor by ID number", description = "A specific doctor returns by their identification number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @Parameters(value = {
            @Parameter(name = "identificationNumber", description = "Doctor's identification number", required = true, example = "12345678")
    })
    @GetMapping("/identification/{identificationNumber}")
    public ResponseEntity<Doctor> getDoctorByIdentificationNumber(@PathVariable String identificationNumber) {
        return doctorService.getAllByDocumentNumber(identificationNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Search for a doctor by type and identification number", description = "A specific doctor returns by their type and identification number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @Parameters(value = {
            @Parameter(name = "documentTypeCode", description = "Code of the identification type", required = true, example = "01"),
            @Parameter(name = "documentNumber", description = "Doctor's identification number", required = true, example = "12345678")
    })
    @GetMapping("/identification/{documentTypeCode}/{documentNumber}")
    public ResponseEntity<Doctor> getDoctorByIdentificationTypeAndNumber(
            @PathVariable String documentTypeCode,
            @PathVariable String documentNumber) {
        return doctorService.getDoctorByDocumentTypeAndDocumentNumber(DocumentType.fromCode(documentTypeCode), documentNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Register a new doctor", description = "Save the information for a new doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Doctor successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid application")
    })
    @PostMapping
    public ResponseEntity<Doctor> create(@RequestBody Doctor doctor) {
        if(doctor.getId() == null || !doctorService.existsById(doctor.getId())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.save(doctor));
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Update a doctor's information", description = "Update a doctor's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid application")
    })
    @PutMapping
    public ResponseEntity<Doctor> update(@RequestBody Doctor doctor) {
        if(doctor.getId() != null && doctorService.existsById(doctor.getId())) {
            return ResponseEntity.ok(doctorService.save(doctor));
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Remove a doctor by ID", description = "Remove the doctor specified by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Doctor successfully removed"),
        @ApiResponse(responseCode = "404", description = "Doctors not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if(doctorService.existsById(id)){
            doctorService.delete(id);
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}

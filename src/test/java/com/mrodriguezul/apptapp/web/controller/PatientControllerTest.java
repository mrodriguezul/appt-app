package com.mrodriguezul.apptapp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrodriguezul.apptapp.domain.exception.PatientAlreadyExistsException;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.service.PatientService;
import com.mrodriguezul.apptapp.web.dto.PatientRequestDto;
import com.mrodriguezul.apptapp.web.dto.PatientResponseDto;
import com.mrodriguezul.apptapp.web.exception.GlobalExceptionHandler;
import com.mrodriguezul.apptapp.web.mapper.PatientWebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {
    @Mock
    private PatientService patientService;
    @Mock
    private PatientWebMapper patientWebMapper;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        PatientController controller = new PatientController(patientService, patientWebMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void post_shouldReturnCreatedPatient() throws Exception {
        PatientRequestDto request = validRequest();
        Patient patient = patient(10L);
        PatientResponseDto response = response(patient);
        when(patientWebMapper.toModel(any(PatientRequestDto.class))).thenReturn(patient);
        when(patientService.create(patient)).thenReturn(patient);
        when(patientWebMapper.toResponse(patient)).thenReturn(response);

        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.documentNumber").value("12345678"))
                .andExpect(jsonPath("$.medicalRecordNumber").value("MR-001"));

        verify(patientService).create(patient);
    }

    @Test
    void put_shouldReturnUpdatedPatient() throws Exception {
        PatientRequestDto request = validRequest();
        Patient patient = patient(10L);
        PatientResponseDto response = response(patient);
        when(patientWebMapper.toModel(any(PatientRequestDto.class))).thenReturn(patient);
        when(patientService.update(10L, patient)).thenReturn(patient);
        when(patientWebMapper.toResponse(patient)).thenReturn(response);

        mockMvc.perform(put("/patient/10")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.medicalRecordNumber").value("MR-001"));

        verify(patientService).update(10L, patient);
    }

    @Test
    void getAll_shouldReturnPatientDtos() throws Exception {
        Patient patient = patient(10L);
        PatientResponseDto response = response(patient);
        when(patientService.getAll(0, 10)).thenReturn(new PageImpl<>(List.of(patient), PageRequest.of(0, 10), 1));
        when(patientWebMapper.toResponse(patient)).thenReturn(response);

        mockMvc.perform(get("/patient"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].medicalRecordNumber").value("MR-001"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void search_shouldReturnPatientDtos() throws Exception {
        Patient patient = patient(10L);
        when(patientService.getAllByNameOrSurname("Ana", "Lopez", 0, 10, "id", "ASC"))
                .thenReturn(new PageImpl<>(List.of(patient), PageRequest.of(0, 10), 1));
        when(patientWebMapper.toResponse(patient)).thenReturn(response(patient));

        mockMvc.perform(get("/patient/search")
                        .param("names", "Ana")
                        .param("surnames", "Lopez"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].names").value("Ana"));
    }

    @Test
    void post_whenRequestIsInvalid_shouldReturnBadRequestWithErrorStructure() throws Exception {
        String invalidJson = """
                {"documentType":"DNI","documentNumber":"","names":"","surnames":"",
                 "dateOfBirth":"2030-01-01","email":"not-an-email","phoneNumber":"",
                 "medicalRecordNumber":""}
                """;

        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid data error"))
                .andExpect(jsonPath("$.message").value("The provided data is not valid"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists());

        verifyNoInteractions(patientService, patientWebMapper);
    }

    @Test
    void getByIdentification_whenPatientDoesNotExist_shouldReturnNotFound() throws Exception {
        when(patientService.getByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenThrow(new ResourceNotFoundException("Patient not found"));

        mockMvc.perform(get("/patient/by-identification/01/12345678"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void post_whenPatientConflictOccurs_shouldReturnConflictWithErrorStructure() throws Exception {
        when(patientWebMapper.toModel(any(PatientRequestDto.class))).thenReturn(patient(null));
        when(patientService.create(any(Patient.class)))
                .thenThrow(new PatientAlreadyExistsException("The person is already a patient"));

        mockMvc.perform(post("/patient")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("The person is already a patient"))
                .andExpect(jsonPath("$.status").value(409));
    }

    private PatientRequestDto validRequest() {
        return new PatientRequestDto(
                DocumentType.DNI, "12345678", "Ana", "Lopez",
                LocalDate.of(1990, 1, 15), "ana@example.com", "999999999", "MR-001");
    }

    private Patient patient(Long id) {
        return Patient.builder()
                .id(id)
                .documentType(DocumentType.DNI)
                .documentNumber("12345678")
                .names("Ana")
                .surnames("Lopez")
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .email("ana@example.com")
                .phoneNumber("999999999")
                .medicalRecordNumber("MR-001")
                .build();
    }

    private PatientResponseDto response(Patient patient) {
        return new PatientResponseDto(
                patient.getId(), patient.getDocumentType(), patient.getDocumentNumber(),
                patient.getNames(), patient.getSurnames(), patient.getDateOfBirth(),
                patient.getEmail(), patient.getPhoneNumber(), patient.getMedicalRecordNumber());
    }
}

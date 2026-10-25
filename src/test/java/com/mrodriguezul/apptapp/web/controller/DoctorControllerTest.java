package com.mrodriguezul.apptapp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrodriguezul.apptapp.domain.exception.DoctorAlreadyExistsException;
import com.mrodriguezul.apptapp.domain.exception.DoctorHasAppointmentsException;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.domain.model.Speciality;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.service.DoctorService;
import com.mrodriguezul.apptapp.web.dto.DoctorRequestDto;
import com.mrodriguezul.apptapp.web.dto.DoctorResponseDto;
import com.mrodriguezul.apptapp.web.dto.SpecialityResponseDto;
import com.mrodriguezul.apptapp.web.exception.GlobalExceptionHandler;
import com.mrodriguezul.apptapp.web.mapper.DoctorWebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {
    @Mock
    private DoctorService doctorService;
    @Mock
    private DoctorWebMapper doctorWebMapper;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DoctorController(doctorService, doctorWebMapper))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_shouldReturnDoctorsWithSpeciality() throws Exception {
        Doctor doctor = doctor(10L);
        when(doctorService.getAll()).thenReturn(List.of(doctor));
        when(doctorWebMapper.toResponse(doctor)).thenReturn(response(doctor));

        mockMvc.perform(get("/doctor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].speciality.id").value(3))
                .andExpect(jsonPath("$[0].speciality.name").value("Cardiology"));
    }

    @Test
    void searchByName_shouldDelegateAndReturnDoctors() throws Exception {
        Doctor doctor = doctor(10L);
        when(doctorService.getAllByNameOrSurname("Ana", "Lopez")).thenReturn(List.of(doctor));
        when(doctorWebMapper.toResponse(doctor)).thenReturn(response(doctor));

        mockMvc.perform(get("/doctor/search/name").param("names", "Ana").param("surnames", "Lopez"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].names").value("Ana"));

        verify(doctorService).getAllByNameOrSurname("Ana", "Lopez");
    }

    @Test
    void searchByName_whenEmpty_shouldReturnNotFound() throws Exception {
        when(doctorService.getAllByNameOrSurname("Unknown", null)).thenReturn(List.of());

        mockMvc.perform(get("/doctor/search/name").param("names", "Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchBySpeciality_shouldReturnDoctors() throws Exception {
        Doctor doctor = doctor(10L);
        when(doctorService.getAllBySpeciality(3L)).thenReturn(List.of(doctor));
        when(doctorWebMapper.toResponse(doctor)).thenReturn(response(doctor));

        mockMvc.perform(get("/doctor/specialty/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].speciality.id").value(3));
    }

    @Test
    void searchBySpeciality_whenEmpty_shouldReturnNotFound() throws Exception {
        when(doctorService.getAllBySpeciality(3L)).thenReturn(List.of());

        mockMvc.perform(get("/doctor/specialty/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldReturnDoctor() throws Exception {
        Doctor doctor = doctor(10L);
        when(doctorService.getDoctor(10L)).thenReturn(Optional.of(doctor));
        when(doctorWebMapper.toResponse(doctor)).thenReturn(response(doctor));

        mockMvc.perform(get("/doctor/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentNumber").value("12345678"));
    }

    @Test
    void getByIdentification_shouldReturnDoctor() throws Exception {
        Doctor doctor = doctor(10L);
        when(doctorService.getAllByDocumentNumber("12345678")).thenReturn(Optional.of(doctor));
        when(doctorWebMapper.toResponse(doctor)).thenReturn(response(doctor));

        mockMvc.perform(get("/doctor/identification/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void getByIdentification_whenMissing_shouldReturnNotFound() throws Exception {
        when(doctorService.getAllByDocumentNumber("12345678")).thenReturn(Optional.empty());

        mockMvc.perform(get("/doctor/identification/12345678"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByIdentificationTypeAndNumber_shouldUseDocumentTypeCode() throws Exception {
        Doctor doctor = doctor(10L);
        when(doctorService.getDoctorByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenReturn(Optional.of(doctor));
        when(doctorWebMapper.toResponse(doctor)).thenReturn(response(doctor));

        mockMvc.perform(get("/doctor/identification/01/12345678"))
                .andExpect(status().isOk());

        verify(doctorService).getDoctorByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678");
    }

    @Test
    void getByIdentificationType_whenCodeIsInvalid_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/doctor/identification/invalid/12345678"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(doctorService);
    }

    @Test
    void getById_whenMissing_shouldReturnNotFound() throws Exception {
        when(doctorService.getDoctor(10L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/doctor/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        DoctorRequestDto request = validRequest();
        Doctor doctor = doctor(null);
        when(doctorWebMapper.toModel(any(DoctorRequestDto.class))).thenReturn(doctor);
        when(doctorService.create(doctor)).thenReturn(doctor(10L));
        when(doctorWebMapper.toResponse(any(Doctor.class))).thenReturn(response(doctor(10L)));

        mockMvc.perform(post("/doctor").contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));

        verify(doctorService).create(doctor);
    }

    @Test
    void update_shouldUsePathVariableId() throws Exception {
        DoctorRequestDto request = validRequest();
        Doctor mapped = doctor(null);
        Doctor updated = doctor(10L);
        when(doctorWebMapper.toModel(any(DoctorRequestDto.class))).thenReturn(mapped);
        when(doctorService.update(10L, mapped)).thenReturn(updated);
        when(doctorWebMapper.toResponse(updated)).thenReturn(response(updated));

        mockMvc.perform(put("/doctor/10").contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));

        verify(doctorService).update(10L, mapped);
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/doctor/10"))
                .andExpect(status().isNoContent());

        verify(doctorService).delete(10L);
    }

    @Test
    void delete_whenAppointmentsExist_shouldReturnConflict() throws Exception {
        doThrow(new DoctorHasAppointmentsException("Doctor cannot be deleted while appointments exist"))
                .when(doctorService).delete(10L);

        mockMvc.perform(delete("/doctor/10"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void create_whenRequestIsInvalid_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/doctor").contentType("application/json")
                        .content("{\"documentNumber\":\"\",\"names\":\"\",\"surnames\":\"\",\"dateOfBirth\":\"2030-01-01\",\"specialityId\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(doctorService, doctorWebMapper);
    }

    @Test
    void create_whenDoctorAlreadyExists_shouldReturnConflict() throws Exception {
        when(doctorWebMapper.toModel(any(DoctorRequestDto.class))).thenReturn(doctor(null));
        when(doctorService.create(any(Doctor.class)))
                .thenThrow(new DoctorAlreadyExistsException("The person is already a doctor"));

        mockMvc.perform(post("/doctor").contentType("application/json")
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void update_whenDoctorDoesNotExist_shouldReturnNotFound() throws Exception {
        when(doctorWebMapper.toModel(any(DoctorRequestDto.class))).thenReturn(doctor(null));
        when(doctorService.update(eq(10L), any(Doctor.class)))
                .thenThrow(new ResourceNotFoundException("Doctor not found"));

        mockMvc.perform(put("/doctor/10").contentType("application/json")
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound());
    }

    private DoctorRequestDto validRequest() {
        return new DoctorRequestDto(DocumentType.DNI, "12345678", "Ana", "Lopez",
                LocalDate.of(1990, 1, 15), "ana@example.com", "999999999", 3L);
    }

    private Doctor doctor(Long id) {
        return Doctor.builder().id(id).documentType(DocumentType.DNI).documentNumber("12345678")
                .names("Ana").surnames("Lopez").dateOfBirth(LocalDate.of(1990, 1, 15))
                .email("ana@example.com").phoneNumber("999999999")
                .speciality(Speciality.builder().id(3L).name("Cardiology").build()).build();
    }

    private DoctorResponseDto response(Doctor doctor) {
        return new DoctorResponseDto(doctor.getId(), doctor.getDocumentType(), doctor.getDocumentNumber(),
                doctor.getNames(), doctor.getSurnames(), doctor.getDateOfBirth(), doctor.getEmail(),
                doctor.getPhoneNumber(), new SpecialityResponseDto(3L, "Cardiology"));
    }
}

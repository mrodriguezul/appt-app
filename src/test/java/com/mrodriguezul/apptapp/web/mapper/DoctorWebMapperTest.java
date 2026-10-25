package com.mrodriguezul.apptapp.web.mapper;

import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.domain.model.Speciality;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.web.dto.DoctorRequestDto;
import com.mrodriguezul.apptapp.web.dto.DoctorResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorWebMapperTest {
    private final SpecialityWebMapper specialityMapper = Mappers.getMapper(SpecialityWebMapper.class);
    private final DoctorWebMapper mapper = new DoctorWebMapperImpl(specialityMapper);

    @Test
    void toModel_shouldMapRequestAndSpecialityId() {
        Doctor result = mapper.toModel(request());

        assertThat(result.getDocumentType()).isEqualTo(DocumentType.DNI);
        assertThat(result.getNames()).isEqualTo("Ana");
        assertThat(result.getSpeciality().getId()).isEqualTo(3L);
    }

    @Test
    void toResponse_shouldMapDoctorAndNestedSpeciality() {
        Doctor doctor = doctor();

        DoctorResponseDto result = mapper.toResponse(doctor);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.documentNumber()).isEqualTo("12345678");
        assertThat(result.speciality().id()).isEqualTo(3L);
        assertThat(result.speciality().name()).isEqualTo("Cardiology");
    }

    @Test
    void mapper_shouldReturnNullForNullInput() {
        assertThat(mapper.toModel(null)).isNull();
        assertThat(mapper.toResponse(null)).isNull();
    }

    private DoctorRequestDto request() {
        return new DoctorRequestDto(DocumentType.DNI, "12345678", "Ana", "Lopez",
                LocalDate.of(1990, 1, 15), "ana@example.com", "999999999", 3L);
    }

    private Doctor doctor() {
        return Doctor.builder().id(10L).documentType(DocumentType.DNI).documentNumber("12345678")
                .names("Ana").surnames("Lopez").dateOfBirth(LocalDate.of(1990, 1, 15))
                .email("ana@example.com").phoneNumber("999999999")
                .speciality(Speciality.builder().id(3L).name("Cardiology").build()).build();
    }
}

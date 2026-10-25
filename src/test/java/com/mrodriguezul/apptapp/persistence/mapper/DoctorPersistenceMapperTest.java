package com.mrodriguezul.apptapp.persistence.mapper;

import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.domain.model.Speciality;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.persistence.entity.DoctorEntity;
import com.mrodriguezul.apptapp.persistence.entity.PersonEntity;
import com.mrodriguezul.apptapp.persistence.entity.SpecialityEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorPersistenceMapperTest {
    private final DoctorPersistenceMapper mapper = Mappers.getMapper(DoctorPersistenceMapper.class);

    DoctorPersistenceMapperTest() {
        ReflectionTestUtils.setField(mapper, "specialityPersistenceMapper",
                Mappers.getMapper(SpecialityPersistenceMapper.class));
    }

    @Test
    void toModel_shouldFlattenPersonAndMapSpeciality() {
        PersonEntity person = person(7L);
        SpecialityEntity speciality = new SpecialityEntity();
        speciality.setId(3L);
        speciality.setName("Cardiology");
        DoctorEntity entity = new DoctorEntity();
        entity.setId(7L);
        entity.setPersonEntity(person);
        entity.setSpecialityEntity(speciality);

        Doctor result = mapper.toModel(entity);

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getNames()).isEqualTo("Ana");
        assertThat(result.getDocumentNumber()).isEqualTo("12345678");
        assertThat(result.getSpeciality().getId()).isEqualTo(3L);
        assertThat(result.getSpeciality().getName()).isEqualTo("Cardiology");
    }

    @Test
    void toEntity_shouldMapPersonAndPropagateId() {
        Doctor doctor = doctor(7L);

        DoctorEntity result = mapper.toEntity(doctor);

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getPersonEntity().getId()).isEqualTo(7L);
        assertThat(result.getPersonEntity().getNames()).isEqualTo("Ana");
        assertThat(result.getSpecialityEntity().getId()).isEqualTo(3L);
        assertThat(result.getSpecialityEntity().getName()).isEqualTo("Cardiology");
        assertThat(result.getAppointments()).isNull();
    }

    @Test
    void mapper_shouldReturnNullForNullInput() {
        assertThat(mapper.toModel(null)).isNull();
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toModel_withNullReferences_shouldKeepNestedValuesNull() {
        DoctorEntity entity = new DoctorEntity();
        entity.setId(7L);

        Doctor result = mapper.toModel(entity);

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getNames()).isNull();
        assertThat(result.getSpeciality()).isNull();
    }

    private PersonEntity person(Long id) {
        PersonEntity person = new PersonEntity();
        person.setId(id);
        person.setDocumentType(DocumentType.DNI);
        person.setDocumentNumber("12345678");
        person.setNames("Ana");
        person.setSurnames("Lopez");
        person.setDateOfBirth(LocalDate.of(1990, 1, 15));
        person.setEmail("ana@example.com");
        person.setPhoneNumber("999999999");
        return person;
    }

    private Doctor doctor(Long id) {
        return Doctor.builder().id(id).documentType(DocumentType.DNI).documentNumber("12345678")
                .names("Ana").surnames("Lopez").dateOfBirth(LocalDate.of(1990, 1, 15))
                .email("ana@example.com").phoneNumber("999999999")
                .speciality(Speciality.builder().id(3L).name("Cardiology").build()).build();
    }
}

package com.mrodriguezul.apptapp.persistence.adapter;

import com.mrodriguezul.apptapp.domain.exception.DoctorAlreadyExistsException;
import com.mrodriguezul.apptapp.domain.exception.PersonDataConflictException;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.domain.model.Speciality;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.persistence.crud.DoctorCrudRepository;
import com.mrodriguezul.apptapp.persistence.crud.PersonCrudRepository;
import com.mrodriguezul.apptapp.persistence.entity.DoctorEntity;
import com.mrodriguezul.apptapp.persistence.entity.PatientEntity;
import com.mrodriguezul.apptapp.persistence.entity.PersonEntity;
import com.mrodriguezul.apptapp.persistence.entity.SpecialityEntity;
import com.mrodriguezul.apptapp.persistence.mapper.DoctorPersistenceMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorRepositoryTest {
    @Mock
    private DoctorCrudRepository doctorCrudRepository;
    @Mock
    private PersonCrudRepository personCrudRepository;
    @Mock
    private DoctorPersistenceMapper doctorPersistenceMapper;
    @Mock
    private EntityManager entityManager;

    private DoctorRepository doctorRepository;

    @BeforeEach
    void setUp() {
        doctorRepository = new DoctorRepository(
                doctorCrudRepository, personCrudRepository, doctorPersistenceMapper, entityManager);
    }

    @Test
    void findAll_shouldMapOrderedEntities() {
        DoctorEntity entity = doctorEntity(person(7L), specialityEntity(3L));
        Doctor doctor = doctor(7L);
        when(doctorCrudRepository.findAllByOrderByIdAsc()).thenReturn(List.of(entity));
        when(doctorPersistenceMapper.toModel(entity)).thenReturn(doctor);

        assertThat(doctorRepository.findAll()).containsExactly(doctor);
    }

    @Test
    void findById_shouldDelegateAndMap() {
        DoctorEntity entity = doctorEntity(person(7L), specialityEntity(3L));
        Doctor doctor = doctor(7L);
        when(doctorCrudRepository.findById(7L)).thenReturn(Optional.of(entity));
        when(doctorPersistenceMapper.toModel(entity)).thenReturn(doctor);

        assertThat(doctorRepository.findById(7L)).containsSame(doctor);
    }

    @Test
    void findByDocumentAndSpeciality_shouldDelegate() {
        DoctorEntity entity = doctorEntity(person(7L), specialityEntity(3L));
        Doctor doctor = doctor(7L);
        when(doctorCrudRepository.findByPersonEntity_DocumentNumberOrderByIdAsc("12345678"))
                .thenReturn(Optional.of(entity));
        when(doctorCrudRepository.findAllBySpecialityEntity_IdOrderByIdAsc(3L))
                .thenReturn(List.of(entity));
        when(doctorPersistenceMapper.toModel(entity)).thenReturn(doctor);

        assertThat(doctorRepository.findByDocumentNumber("12345678")).containsSame(doctor);
        assertThat(doctorRepository.findAllBySpeciality(3L)).containsExactly(doctor);
    }

    @Test
    void findByNameOrSurname_shouldDelegateAndMap() {
        DoctorEntity entity = doctorEntity(person(7L), specialityEntity(3L));
        Doctor doctor = doctor(7L);
        when(doctorCrudRepository
                .findAllByPersonEntity_namesContainingIgnoreCaseOrPersonEntity_surnamesContainingIgnoreCaseOrderByIdAsc("Ana", "Lopez"))
                .thenReturn(List.of(entity));
        when(doctorPersistenceMapper.toModel(entity)).thenReturn(doctor);

        assertThat(doctorRepository.findAllByNameOrSurname("Ana", "Lopez")).containsExactly(doctor);
    }

    @Test
    void save_whenNew_shouldCreatePersonAndDoctor() {
        Doctor doctor = doctor(null);
        DoctorEntity mapped = doctorEntity(person(null), specialityEntity(3L));
        DoctorEntity saved = doctorEntity(person(7L), specialityEntity(3L));
        PersonEntity mappedPerson = mapped.getPersonEntity();
        when(doctorPersistenceMapper.toEntity(doctor)).thenReturn(mapped);
        when(entityManager.find(SpecialityEntity.class, 3L)).thenReturn(mapped.getSpecialityEntity());
        when(personCrudRepository.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenReturn(Optional.empty());
        when(personCrudRepository.save(mappedPerson)).thenReturn(saved.getPersonEntity());
        when(doctorCrudRepository.save(mapped)).thenReturn(saved);
        when(doctorPersistenceMapper.toModel(saved)).thenReturn(doctor(7L));

        assertThat(doctorRepository.save(doctor).getId()).isEqualTo(7L);
        verify(personCrudRepository).save(mappedPerson);
        verify(doctorCrudRepository).save(mapped);
        assertThat(mapped.getPersonEntity()).isSameAs(saved.getPersonEntity());
    }

    @Test
    void save_whenPersonExistsAsPatient_shouldReusePerson() {
        Doctor doctor = doctor(null);
        PersonEntity existing = person(7L);
        existing.setPatientEntity(new PatientEntity());
        DoctorEntity mapped = doctorEntity(person(null), specialityEntity(3L));
        when(doctorPersistenceMapper.toEntity(doctor)).thenReturn(mapped);
        when(entityManager.find(SpecialityEntity.class, 3L)).thenReturn(mapped.getSpecialityEntity());
        when(personCrudRepository.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenReturn(Optional.of(existing));
        when(doctorCrudRepository.save(mapped)).thenReturn(mapped);
        when(doctorPersistenceMapper.toModel(mapped)).thenReturn(doctor(7L));

        doctorRepository.save(doctor);

        verify(personCrudRepository, never()).save(any(PersonEntity.class));
        verify(doctorCrudRepository).save(mapped);
        assertThat(mapped.getPersonEntity()).isSameAs(existing);
    }

    @Test
    void save_whenPersonAlreadyDoctor_shouldReject() {
        Doctor doctor = doctor(null);
        PersonEntity existing = person(7L);
        existing.setDoctorEntity(new DoctorEntity());
        DoctorEntity mapped = doctorEntity(person(null), specialityEntity(3L));
        when(doctorPersistenceMapper.toEntity(doctor)).thenReturn(mapped);
        when(entityManager.find(SpecialityEntity.class, 3L)).thenReturn(mapped.getSpecialityEntity());
        when(personCrudRepository.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> doctorRepository.save(doctor))
                .isInstanceOf(DoctorAlreadyExistsException.class);
        verifyNoInteractions(doctorCrudRepository);
    }

    @Test
    void save_whenPersonalDataDiffers_shouldReject() {
        Doctor doctor = doctor(null);
        PersonEntity existing = person(7L);
        existing.setEmail("other@example.com");
        DoctorEntity mapped = doctorEntity(person(null), specialityEntity(3L));
        when(doctorPersistenceMapper.toEntity(doctor)).thenReturn(mapped);
        when(entityManager.find(SpecialityEntity.class, 3L)).thenReturn(mapped.getSpecialityEntity());
        when(personCrudRepository.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "12345678"))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> doctorRepository.save(doctor))
                .isInstanceOf(PersonDataConflictException.class);
        verifyNoInteractions(doctorCrudRepository);
    }

    @Test
    void save_whenSpecialityDoesNotExist_shouldReject() {
        Doctor doctor = doctor(null);
        when(doctorPersistenceMapper.toEntity(doctor)).thenReturn(doctorEntity(person(null), specialityEntity(3L)));
        when(entityManager.find(SpecialityEntity.class, 3L)).thenReturn(null);

        assertThatThrownBy(() -> doctorRepository.save(doctor))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Speciality not found");
    }

    @Test
    void update_shouldPreserveIdentificationAndUpdatePersonAndSpeciality() {
        Doctor doctor = doctor(7L, 4L);
        DoctorEntity existing = doctorEntity(person(7L), specialityEntity(3L));
        DoctorEntity mapped = doctorEntity(person(7L), specialityEntity(4L));
        when(doctorPersistenceMapper.toEntity(doctor)).thenReturn(mapped);
        when(entityManager.find(SpecialityEntity.class, 4L)).thenReturn(mapped.getSpecialityEntity());
        when(doctorCrudRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(personCrudRepository.save(existing.getPersonEntity())).thenReturn(existing.getPersonEntity());
        when(doctorCrudRepository.save(existing)).thenReturn(existing);
        when(doctorPersistenceMapper.toModel(existing)).thenReturn(doctor);

        doctorRepository.save(doctor);

        verify(personCrudRepository).save(existing.getPersonEntity());
        verify(doctorCrudRepository).save(existing);
        assertThat(existing.getSpecialityEntity().getId()).isEqualTo(4L);
    }

    @Test
    void update_whenDoctorDoesNotExist_shouldReject() {
        Doctor doctor = doctor(7L);
        when(doctorPersistenceMapper.toEntity(doctor)).thenReturn(doctorEntity(person(7L), specialityEntity(3L)));
        when(entityManager.find(SpecialityEntity.class, 3L)).thenReturn(specialityEntity(3L));
        when(doctorCrudRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorRepository.save(doctor))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldOnlyDeleteDoctor() {
        doctorRepository.delete(7L);

        verify(doctorCrudRepository).deleteById(7L);
        verifyNoInteractions(personCrudRepository);
    }

    private Doctor doctor(Long id) {
        return doctor(id, 3L);
    }

    private Doctor doctor(Long id, Long specialityId) {
        return Doctor.builder().id(id).documentType(DocumentType.DNI).documentNumber("12345678")
                .names("Ana").surnames("Lopez").dateOfBirth(LocalDate.of(1990, 1, 15))
                .email("ana@example.com").phoneNumber("999999999")
                .speciality(Speciality.builder().id(specialityId).name("Cardiology").build()).build();
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

    private SpecialityEntity specialityEntity(Long id) {
        SpecialityEntity speciality = new SpecialityEntity();
        speciality.setId(id);
        speciality.setName(id == 3L ? "Cardiology" : "Neurology");
        return speciality;
    }

    private DoctorEntity doctorEntity(PersonEntity person, SpecialityEntity speciality) {
        DoctorEntity doctor = new DoctorEntity();
        doctor.setId(person.getId());
        doctor.setPersonEntity(person);
        doctor.setSpecialityEntity(speciality);
        return doctor;
    }
}

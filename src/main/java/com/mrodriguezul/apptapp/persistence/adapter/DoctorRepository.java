package com.mrodriguezul.apptapp.persistence.adapter;

import com.mrodriguezul.apptapp.domain.exception.DoctorAlreadyExistsException;
import com.mrodriguezul.apptapp.domain.exception.PersonDataConflictException;
import com.mrodriguezul.apptapp.domain.exception.ResourceNotFoundException;
import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.domain.model.Speciality;
import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.domain.repository.IDoctorRepository;
import com.mrodriguezul.apptapp.persistence.crud.DoctorCrudRepository;
import com.mrodriguezul.apptapp.persistence.crud.PersonCrudRepository;
import com.mrodriguezul.apptapp.persistence.entity.DoctorEntity;
import com.mrodriguezul.apptapp.persistence.entity.PersonEntity;
import com.mrodriguezul.apptapp.persistence.entity.SpecialityEntity;
import com.mrodriguezul.apptapp.persistence.mapper.DoctorPersistenceMapper;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class DoctorRepository implements IDoctorRepository {
    private final DoctorCrudRepository doctorCrudRepository;
    private final PersonCrudRepository personCrudRepository;
    private final DoctorPersistenceMapper doctorPersistenceMapper;
    private final EntityManager entityManager;

    public DoctorRepository(
            DoctorCrudRepository doctorCrudRepository,
            PersonCrudRepository personCrudRepository,
            DoctorPersistenceMapper doctorPersistenceMapper,
            EntityManager entityManager) {
        this.doctorCrudRepository = doctorCrudRepository;
        this.personCrudRepository = personCrudRepository;
        this.doctorPersistenceMapper = doctorPersistenceMapper;
        this.entityManager = entityManager;
    }

    @Override
    public List<Doctor> findAll() {
        return doctorCrudRepository.findAllByOrderByIdAsc()
                .stream()
                .map(doctorPersistenceMapper::toModel)
                .toList();
    }

    @Override
    public List<Doctor> findAllByNameOrSurname(String names, String surnames) {
        return doctorCrudRepository.findAllByPersonEntity_namesContainingIgnoreCaseOrPersonEntity_surnamesContainingIgnoreCaseOrderByIdAsc(names, surnames)
                .stream()
                .map(doctorPersistenceMapper::toModel)
                .toList();
    }

    @Override
    public List<Doctor> findAllBySpeciality(Long specialityId) {
        return doctorCrudRepository.findAllBySpecialityEntity_IdOrderByIdAsc(specialityId)
                .stream()
                .map(doctorPersistenceMapper::toModel)
                .toList();
    }

    @Override
    public Optional<Doctor> findByDocumentNumber(String documentNumber) {
        return doctorCrudRepository.findByPersonEntity_DocumentNumberOrderByIdAsc(documentNumber)
                .map(doctorPersistenceMapper::toModel);
    }


    @Override
    public Optional<Doctor> findById(Long id) {
        return doctorCrudRepository.findById(id)
                .map(doctorPersistenceMapper::toModel);
    }

    @Override
    public Optional<Doctor> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber) {
        return doctorCrudRepository.findByPersonEntity_DocumentTypeAndPersonEntity_DocumentNumberOrderByIdAsc(documentType, documentNumber)
                .map(doctorPersistenceMapper::toModel);
    }

    @Override
    @Transactional
    public Doctor save(Doctor doctor) {
        DoctorEntity doctorEntity = doctorPersistenceMapper.toEntity(doctor);
        SpecialityEntity specialityEntity = resolveSpeciality(doctor);

        if (doctor.getId() != null) {
            return update(doctor, doctorEntity, specialityEntity);
        }

        PersonEntity personEntity = personCrudRepository
                .findByDocumentTypeAndDocumentNumber(doctor.getDocumentType(), doctor.getDocumentNumber())
                .map(existingPerson -> {
                    if (existingPerson.getDoctorEntity() != null) {
                        throw new DoctorAlreadyExistsException("The person is already a doctor");
                    }
                    validatePersonalData(doctor, existingPerson);
                    return existingPerson;
                })
                .orElseGet(() -> personCrudRepository.save(doctorEntity.getPersonEntity()));

        doctorEntity.setPersonEntity(personEntity);
        doctorEntity.setSpecialityEntity(specialityEntity);
        return doctorPersistenceMapper.toModel(doctorCrudRepository.save(doctorEntity));
    }

    private Doctor update(Doctor doctor, DoctorEntity mappedDoctor, SpecialityEntity specialityEntity) {
        DoctorEntity existingDoctor = doctorCrudRepository.findById(doctor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        PersonEntity existingPerson = existingDoctor.getPersonEntity();
        PersonEntity mappedPerson = mappedDoctor.getPersonEntity();

        existingPerson.setNames(mappedPerson.getNames());
        existingPerson.setSurnames(mappedPerson.getSurnames());
        existingPerson.setDateOfBirth(mappedPerson.getDateOfBirth());
        existingPerson.setEmail(mappedPerson.getEmail());
        existingPerson.setPhoneNumber(mappedPerson.getPhoneNumber());
        existingDoctor.setSpecialityEntity(specialityEntity);

        personCrudRepository.save(existingPerson);
        return doctorPersistenceMapper.toModel(doctorCrudRepository.save(existingDoctor));
    }

    private SpecialityEntity resolveSpeciality(Doctor doctor) {
        return Optional.ofNullable(doctor.getEspecialidad())
                .map(Speciality::getId)
                .map(id -> entityManager.find(SpecialityEntity.class, id))
                .orElseThrow(() -> new ResourceNotFoundException("Speciality not found"));
    }

    private void validatePersonalData(Doctor doctor, PersonEntity personEntity) {
        if (Stream.of(
                        Objects.equals(doctor.getNames(), personEntity.getNames()),
                        Objects.equals(doctor.getSurnames(), personEntity.getSurnames()),
                        Objects.equals(doctor.getDateOfBirth(), personEntity.getDateOfBirth()),
                        Objects.equals(doctor.getEmail(), personEntity.getEmail()),
                        Objects.equals(doctor.getPhoneNumber(), personEntity.getPhoneNumber()))
                .anyMatch(matches -> !matches)) {
            throw new PersonDataConflictException("Personal data does not match the existing person");
        }
    }

    @Override
    public void delete(Long id) {
        doctorCrudRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return doctorCrudRepository.existsById(id);
    }

}

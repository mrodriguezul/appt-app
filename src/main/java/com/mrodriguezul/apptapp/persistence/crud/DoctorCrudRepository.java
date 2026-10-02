package com.mrodriguezul.apptapp.persistence.crud;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.persistence.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorCrudRepository extends JpaRepository<DoctorEntity, Long> {
    List<DoctorEntity> findAllByOrderByIdAsc();
    List<DoctorEntity> findAllByPersonEntity_namesContainingIgnoreCaseOrPersonEntity_surnamesContainingIgnoreCaseOrderByIdAsc(String names, String surnames);
    List<DoctorEntity> findAllBySpecialityEntity_IdOrderByIdAsc(Long specialityId);
    List<DoctorEntity> findAllBySpecialityEntity_IdInOrderByIdAsc(List<Long> specialityIds);
    Optional<DoctorEntity> findByPersonEntity_DocumentNumberOrderByIdAsc(String documentNumber);
    Optional<DoctorEntity> findByPersonEntity_DocumentTypeAndPersonEntity_DocumentNumberOrderByIdAsc(DocumentType documentType, String documentNumber);
    int countAllBySpecialityEntity_Id(Long specialityId);
    //int countAllByPersonEntity_IdentificationEntityId(Long identificationId);
}
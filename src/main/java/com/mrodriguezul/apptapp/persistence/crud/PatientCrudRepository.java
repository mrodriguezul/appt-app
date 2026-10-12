package com.mrodriguezul.apptapp.persistence.crud;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.persistence.entity.PatientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientCrudRepository extends JpaRepository<PatientEntity, Long> {
    Page<PatientEntity> findAllByPersonEntity_DocumentTypeAndPersonEntity_DocumentNumberOrderByIdAsc(DocumentType documentType, String documentNumber, Pageable pageable);
    Optional<PatientEntity> findByPersonEntity_DocumentTypeAndPersonEntity_DocumentNumber(DocumentType documentType, String documentNumber);
    @EntityGraph(attributePaths = "personEntity")
    Optional<PatientEntity> findById(Long id);
}

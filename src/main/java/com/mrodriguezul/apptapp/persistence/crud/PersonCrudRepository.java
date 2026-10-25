package com.mrodriguezul.apptapp.persistence.crud;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.persistence.entity.PersonEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonCrudRepository extends JpaRepository<PersonEntity, Long> {
    boolean existsByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber);
    @EntityGraph(attributePaths = {"patientEntity", "doctorEntity"})
    Optional<PersonEntity> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber);
}

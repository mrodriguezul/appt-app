package com.mrodriguezul.apptapp.persistence.crud;

import com.mrodriguezul.apptapp.domain.model.enums.DocumentType;
import com.mrodriguezul.apptapp.persistence.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonCrudRepository extends JpaRepository<PersonEntity, Long> {
    List<PersonEntity> findByDocumentNumberOrderByDocumentNumberAsc(String documentNumber);
    Optional<PersonEntity> findPersonEntitiesByDocumentTypeAndDocumentNumberOrderByDocumentNumberAsc(DocumentType documentType, String documentNumber);
}

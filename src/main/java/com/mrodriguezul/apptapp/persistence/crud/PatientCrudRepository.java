package com.mrodriguezul.apptapp.persistence.crud;

import com.mrodriguezul.apptapp.persistence.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientCrudRepository extends JpaRepository<PatientEntity, Long> {
}

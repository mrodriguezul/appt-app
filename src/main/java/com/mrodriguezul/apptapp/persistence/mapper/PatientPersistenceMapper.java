package com.mrodriguezul.apptapp.persistence.mapper;

import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.persistence.entity.PatientEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PatientPersistenceMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "personEntity.documentType", target = "documentType")
    @Mapping(source = "personEntity.documentNumber", target = "documentNumber")
    @Mapping(source = "personEntity.names", target = "names")
    @Mapping(source = "personEntity.surnames", target = "surnames")
    @Mapping(source = "personEntity.dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "personEntity.email", target = "email")
    @Mapping(source = "personEntity.phoneNumber", target = "phoneNumber")
    Patient toModel(PatientEntity patientEntity);

    @InheritInverseConfiguration(name = "toModel")
    @Mapping(source = "id", target = "personEntity.id")
    @Mapping(target = "appointments", ignore = true)
    PatientEntity toEntity(Patient patient);
}

package com.mrodriguezul.apptapp.persistence.mapper;

import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.persistence.entity.PatientEntity;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {PersonPersistenceMapper.class})
public interface PatientPersistenceMapper {
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "personEntity.documentType", target = "documentType"),
        @Mapping(source = "personEntity.documentNumber", target = "documentNumber"),
        @Mapping(source = "personEntity.names", target = "nombres"),
        @Mapping(source = "personEntity.surnames", target = "apellidos"),
        @Mapping(source = "personEntity.dateOfBirth", target = "fechaNacimiento"),
        @Mapping(source = "personEntity.email", target = "email"),
        @Mapping(source = "personEntity.phoneNumber", target = "numeroTelefono")
    })
    Patient toPaciente(PatientEntity patientEntity);

    @InheritConfiguration
    @Mappings({
            @Mapping(source = "id", target = "personEntity.id"),
            @Mapping(target = "appointments", ignore = true)
    })
    PatientEntity toPatientEntity(Patient patient);
}

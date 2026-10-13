package com.mrodriguezul.apptapp.persistence.mapper;

import com.mrodriguezul.apptapp.domain.model.Appointment;
import com.mrodriguezul.apptapp.persistence.entity.AppointmentEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AppointmentPersistenceMapper {
    @Mapping(source = "doctorEntity.id", target = "doctorId")
    @Mapping(source = "patientEntity.id", target = "patientId")
    Appointment toModel(AppointmentEntity entity);

    @Mapping(target = "doctorEntity", ignore = true)
    @Mapping(target = "patientEntity", ignore = true)
    AppointmentEntity toEntity(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctorEntity", ignore = true)
    @Mapping(target = "patientEntity", ignore = true)
    void updateEntity(Appointment appointment, @MappingTarget AppointmentEntity entity);
}

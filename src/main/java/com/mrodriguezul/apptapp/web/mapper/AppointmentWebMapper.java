package com.mrodriguezul.apptapp.web.mapper;

import com.mrodriguezul.apptapp.domain.model.Appointment;
import com.mrodriguezul.apptapp.web.dto.AppointmentRequestDto;
import com.mrodriguezul.apptapp.web.dto.AppointmentResponseDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AppointmentWebMapper {
    @Mapping(target = "id", ignore = true)
    Appointment toModel(AppointmentRequestDto request);

    AppointmentResponseDto toResponse(Appointment appointment);
}

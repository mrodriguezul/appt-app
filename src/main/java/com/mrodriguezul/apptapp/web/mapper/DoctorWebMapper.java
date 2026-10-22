package com.mrodriguezul.apptapp.web.mapper;

import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.web.dto.DoctorRequestDto;
import com.mrodriguezul.apptapp.web.dto.DoctorResponseDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DoctorWebMapper {
    Doctor toModel(DoctorRequestDto request);

    DoctorResponseDto toResponse(Doctor doctor);
}

package com.mrodriguezul.apptapp.web.mapper;

import com.mrodriguezul.apptapp.domain.model.Doctor;
import com.mrodriguezul.apptapp.web.dto.DoctorRequestDto;
import com.mrodriguezul.apptapp.web.dto.DoctorResponseDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {SpecialityWebMapper.class}
)
public interface DoctorWebMapper {
    @Mapping(target = "speciality", source = "specialityId")
    Doctor toModel(DoctorRequestDto request);

    DoctorResponseDto toResponse(Doctor doctor);
}

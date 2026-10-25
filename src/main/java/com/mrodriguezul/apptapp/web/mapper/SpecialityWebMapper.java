package com.mrodriguezul.apptapp.web.mapper;

import com.mrodriguezul.apptapp.domain.model.Speciality;
import com.mrodriguezul.apptapp.web.dto.SpecialityResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpecialityWebMapper {
    SpecialityResponseDto toResponse(Speciality speciality);

    @Mapping(target = "id", source = ".")
    Speciality toModel(Long specialityId);
}

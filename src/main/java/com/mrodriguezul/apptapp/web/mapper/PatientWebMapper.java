package com.mrodriguezul.apptapp.web.mapper;

import com.mrodriguezul.apptapp.domain.model.Patient;
import com.mrodriguezul.apptapp.web.dto.PatientRequestDto;
import com.mrodriguezul.apptapp.web.dto.PatientResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientWebMapper {

    @Mapping(target = "id", ignore = true)
    Patient toModel(PatientRequestDto request);

    PatientResponseDto toResponse(Patient patient);
}

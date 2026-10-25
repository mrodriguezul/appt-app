package com.mrodriguezul.apptapp.web.mapper;

import com.mrodriguezul.apptapp.domain.model.Speciality;
import com.mrodriguezul.apptapp.web.dto.SpecialityResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialityWebMapperTest {
    private final SpecialityWebMapper mapper = Mappers.getMapper(SpecialityWebMapper.class);

    @Test
    void toResponse_shouldMapIdAndName() {
        SpecialityResponseDto result = mapper.toResponse(Speciality.builder().id(3L).name("Cardiology").build());

        assertThat(result.id()).isEqualTo(3L);
        assertThat(result.name()).isEqualTo("Cardiology");
    }

    @Test
    void toModel_shouldUseLongAsId() {
        Speciality result = mapper.toModel(3L);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isNull();
    }

    @Test
    void mapper_shouldReturnNullForNullInput() {
        assertThat(mapper.toResponse(null)).isNull();
        assertThat(mapper.toModel(null)).isNull();
    }
}

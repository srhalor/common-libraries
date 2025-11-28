package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.ReferenceDataDto;
import com.shdev.omsdatabase.dto.ReferenceDataLiteDto;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidatorFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DtoMapperTest {

    private final ReferenceDataMapper mapper = Mappers.getMapper(ReferenceDataMapper.class);

    @Test
    void refData_roundTrip() {
        ReferenceDataEntity e = new ReferenceDataEntity();
        e.setId(10L);
        e.setRefDataType("DOCUMENT_TYPE");
        e.setRefDataValue("INVOICE");
        e.setDescription("Invoice docs");
        e.setEditable(true);
        e.setEffectFromDat(LocalDate.of(2024,1,1));
        e.setEffectToDat(LocalDate.of(4712,12,31));

        ReferenceDataDto dto = mapper.toDto(e);
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.refDataType()).isEqualTo("DOCUMENT_TYPE");
        assertThat(dto.refDataValue()).isEqualTo("INVOICE");
        assertThat(dto.editable()).isTrue();

        ReferenceDataEntity back = mapper.toEntity(dto);
        assertThat(back.getRefDataValue()).isEqualTo("INVOICE");
        assertThat(back.getRefDataType()).isEqualTo("DOCUMENT_TYPE");
    }

    @Test
    void validation_detectsTooLong() {
        String longType = "X".repeat(60); // exceeds 50
        ReferenceDataDto bad = new ReferenceDataDto(null, longType, "NAME", null, true,
                LocalDate.now(), LocalDate.now().plusDays(1), Instant.now(), Instant.now(), null, null);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<ReferenceDataDto>> violations = validator.validate(bad);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("refDataType"))).isTrue();
        }
    }

    @Test
    void lite_mapping() {
        ReferenceDataEntity e = new ReferenceDataEntity();
        e.setId(5L);
        e.setRefDataValue("IV");
        ReferenceDataLiteDto lite = mapper.toLite(e);
        assertThat(lite.id()).isEqualTo(5L);
        assertThat(lite.refDataValue()).isEqualTo("IV");
    }
}

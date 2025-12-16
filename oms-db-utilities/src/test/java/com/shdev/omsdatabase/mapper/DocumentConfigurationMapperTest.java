package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.DocumentConfigInDto;
import com.shdev.omsdatabase.dto.DocumentConfigOutDto;
import com.shdev.omsdatabase.entity.DocumentConfigEntity;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DocumentConfigurationMapper.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
class DocumentConfigurationMapperTest {

    @Autowired
    private DocumentConfigurationMapper mapper;

    @Test
    void toEntity_createMapping_populatesReferenceEntities() {
        DocumentConfigInDto dto = new DocumentConfigInDto(
                1L, 2L, 3L,
                "VAL", "Desc",
                LocalDate.of(2024,1,1), LocalDate.of(2024,12,31)
        );
        DocumentConfigEntity entity = mapper.toEntity(dto);
        assertThat(entity.getOmrdaFooter()).isNotNull();
        assertThat(entity.getOmrdaFooter().getId()).isEqualTo(1L);
        assertThat(entity.getOmrdaAppDocSpec().getId()).isEqualTo(2L);
        assertThat(entity.getOmrdaCode().getId()).isEqualTo(3L);
        assertThat(entity.getValue()).isEqualTo("VAL");
        assertThat(entity.getDescription()).isEqualTo("Desc");
        assertThat(entity.getEffectFromDat()).isEqualTo(LocalDate.of(2024,1,1));
        assertThat(entity.getEffectToDat()).isEqualTo(LocalDate.of(2024,12,31));
    }

    @Test
    void toDto_mapsNestedReferenceData() {
        DocumentConfigEntity e = new DocumentConfigEntity();
        e.setId(10L);
        e.setOmrdaFooter(ReferenceDataEntity.builder().id(5L).refDataValue("FTR").description("Footer").build());
        e.setOmrdaAppDocSpec(ReferenceDataEntity.builder().id(6L).refDataValue("SPEC").description("Spec").build());
        e.setOmrdaCode(ReferenceDataEntity.builder().id(7L).refDataValue("CODE").description("CodeDesc").build());
        e.setValue("CONFIG_VAL");
        e.setDescription("Config description");
        e.setEffectFromDat(LocalDate.of(2024,1,1));
        e.setEffectToDat(LocalDate.of(2024,12,31));

        DocumentConfigOutDto dto = mapper.toDto(e);
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.footer().id()).isEqualTo(5L);
        assertThat(dto.code().refDataValue()).isEqualTo("CODE");
        assertThat(dto.value()).isEqualTo("CONFIG_VAL");
        assertThat(dto.desc()).isEqualTo("Config description");
    }

    @Test
    void updateEntity_partialUpdate_ignoresNullsAndUpdatesProvidedFields() {
        DocumentConfigEntity existing = new DocumentConfigEntity();
        existing.setOmrdaFooter(ReferenceDataEntity.builder().id(11L).build());
        existing.setOmrdaAppDocSpec(ReferenceDataEntity.builder().id(12L).build());
        existing.setOmrdaCode(ReferenceDataEntity.builder().id(13L).build());
        existing.setValue("OLD_VAL");
        existing.setDescription("OLD_DESC");
        existing.setEffectFromDat(LocalDate.of(2024,1,1));
        existing.setEffectToDat(LocalDate.of(2024,6,30));

        DocumentConfigInDto patch = new DocumentConfigInDto(
                null,
                20L,
                null,
                "NEW_VAL",
                null,
                null,
                LocalDate.of(2024,12,31)
        );

        mapper.updateEntity(patch, existing);

        assertThat(existing.getOmrdaFooter().getId()).isEqualTo(11L);
        assertThat(existing.getOmrdaAppDocSpec().getId()).isEqualTo(20L);
        assertThat(existing.getOmrdaCode().getId()).isEqualTo(13L);
        assertThat(existing.getValue()).isEqualTo("NEW_VAL");
        assertThat(existing.getDescription()).isEqualTo("OLD_DESC");
        assertThat(existing.getEffectFromDat()).isEqualTo(LocalDate.of(2024,1,1));
        assertThat(existing.getEffectToDat()).isEqualTo(LocalDate.of(2024,12,31));
    }
}

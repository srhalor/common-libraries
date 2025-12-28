package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.DocumentConfigInDto;
import com.shdev.omsdatabase.dto.DocumentConfigOutDto;
import com.shdev.omsdatabase.entity.DocumentConfigEntity;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link DocumentConfigurationMapper} covering create, read, and partial update mappings.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
@DisplayName("DocumentConfigurationMapper unit tests")
class DocumentConfigurationMapperTest {

    @Autowired
    private DocumentConfigurationMapper mapper;

    @Test
    @DisplayName("toEntity: creates entity with reference IDs and value fields")
    void toEntity_createMapping_populatesReferenceEntities() {
        DocumentConfigInDto dto = new DocumentConfigInDto(
                1L, 2L, 3L,
                "VAL", "Desc",
                OffsetDateTime.parse("2024-01-01T00:00:00Z"), OffsetDateTime.parse("2024-12-31T23:59:59Z")
        );
        DocumentConfigEntity entity = mapper.toEntity(dto);
        assertThat(entity.getOmrdaFooter()).isNotNull();
        assertThat(entity.getOmrdaFooter().getId()).isEqualTo(1L);
        assertThat(entity.getOmrdaAppDocSpec().getId()).isEqualTo(2L);
        assertThat(entity.getOmrdaCode().getId()).isEqualTo(3L);
        assertThat(entity.getValue()).isEqualTo("VAL");
        assertThat(entity.getDescription()).isEqualTo("Desc");
        assertThat(entity.getEffectFromDat()).isEqualTo(OffsetDateTime.parse("2024-01-01T00:00:00Z"));
        assertThat(entity.getEffectToDat()).isEqualTo(OffsetDateTime.parse("2024-12-31T23:59:59Z"));
    }

    @Test
    @DisplayName("toDto: flattens nested reference data into DTO")
    void toDto_mapsNestedReferenceData() {
        DocumentConfigEntity e = new DocumentConfigEntity();
        e.setId(10L);
        e.setOmrdaFooter(ReferenceDataEntity.builder().id(5L).refDataValue("FTR").description("Footer").build());
        e.setOmrdaAppDocSpec(ReferenceDataEntity.builder().id(6L).refDataValue("SPEC").description("Spec").build());
        e.setOmrdaCode(ReferenceDataEntity.builder().id(7L).refDataValue("CODE").description("CodeDesc").build());
        e.setValue("CONFIG_VAL");
        e.setDescription("Config description");
        e.setEffectFromDat(OffsetDateTime.parse("2024-01-01T00:00:00Z"));
        e.setEffectToDat(OffsetDateTime.parse("2024-12-31T23:59:59Z"));

        DocumentConfigOutDto dto = mapper.toDto(e);
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.footer().id()).isEqualTo(5L);
        assertThat(dto.code().refDataValue()).isEqualTo("CODE");
        assertThat(dto.value()).isEqualTo("CONFIG_VAL");
        assertThat(dto.desc()).isEqualTo("Config description");
    }

    /**
     * Test: updateEntity applies partial update ignoring null fields
     * Given: Existing DocumentConfigEntity with all fields populated and patch DTO with selective updates
     * When: updateEntity is called
     * Then: Only non-null fields from DTO are applied, existing values remain for null fields
     */
    @Test
    @DisplayName("updateEntity: ignores nulls and updates only provided fields")
    void updateEntity_partialUpdate_ignoresNullsAndUpdatesProvidedFields() {
        DocumentConfigEntity existing = new DocumentConfigEntity();
        existing.setOmrdaFooter(ReferenceDataEntity.builder().id(11L).build());
        existing.setOmrdaAppDocSpec(ReferenceDataEntity.builder().id(12L).build());
        existing.setOmrdaCode(ReferenceDataEntity.builder().id(13L).build());
        existing.setValue("OLD_VAL");
        existing.setDescription("OLD_DESC");
        existing.setEffectFromDat(OffsetDateTime.parse("2024-01-01T00:00:00Z"));
        existing.setEffectToDat(OffsetDateTime.parse("2024-06-30T23:59:59Z"));

        DocumentConfigInDto patch = new DocumentConfigInDto(
                null,
                20L,
                null,
                "NEW_VAL",
                null,
                null,
                OffsetDateTime.parse("2024-12-31T23:59:59Z")
        );

        mapper.updateEntity(patch, existing);

        assertThat(existing.getOmrdaFooter().getId()).isEqualTo(11L);
        assertThat(existing.getOmrdaAppDocSpec().getId()).isEqualTo(20L);
        assertThat(existing.getOmrdaCode().getId()).isEqualTo(13L);
        assertThat(existing.getValue()).isEqualTo("NEW_VAL");
        assertThat(existing.getDescription()).isEqualTo("OLD_DESC");
        assertThat(existing.getEffectFromDat()).isEqualTo(OffsetDateTime.parse("2024-01-01T00:00:00Z"));
        assertThat(existing.getEffectToDat()).isEqualTo(OffsetDateTime.parse("2024-12-31T23:59:59Z"));
    }
}

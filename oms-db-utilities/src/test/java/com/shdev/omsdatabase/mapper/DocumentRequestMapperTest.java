package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.DocumentRequestInDto;
import com.shdev.omsdatabase.dto.DocumentRequestOutDto;
import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
class DocumentRequestMapperTest {

    @Autowired
    private DocumentRequestMapper mapper;

    @Test
    void toEntity_createMapping_populatesReferenceEntities() {
        DocumentRequestInDto dto = new DocumentRequestInDto(1L, 2L, 3L, 4L);
        DocumentRequestEntity entity = mapper.toEntity(dto);
        assertThat(entity.getOmrdaSourceSystem().getId()).isEqualTo(1L);
        assertThat(entity.getOmrdaDocumentType().getId()).isEqualTo(2L);
        assertThat(entity.getOmrdaDocumentName().getId()).isEqualTo(3L);
        assertThat(entity.getOmrdaDocStatus().getId()).isEqualTo(4L);
    }

    @Test
    void toDto_mapsNestedReferenceData() {
        DocumentRequestEntity e = new DocumentRequestEntity();
        e.setId(99L);
        e.setOmrdaSourceSystem(ReferenceDataEntity.builder().id(10L).refDataValue("SRC").description("Source").build());
        e.setOmrdaDocumentType(ReferenceDataEntity.builder().id(20L).refDataValue("TYPE").description("Type").build());
        e.setOmrdaDocumentName(ReferenceDataEntity.builder().id(30L).refDataValue("NAME").description("Name").build());
        e.setOmrdaDocStatus(ReferenceDataEntity.builder().id(40L).refDataValue("ST").description("Status").build());

        DocumentRequestOutDto dto = mapper.toDto(e);
        assertThat(dto.id()).isEqualTo(99L);
        assertThat(dto.sourceSystem().id()).isEqualTo(10L);
        assertThat(dto.documentType().refDataValue()).isEqualTo("TYPE");
        assertThat(dto.documentStatus().refDataValue()).isEqualTo("ST");
    }

    @Test
    void updateStatus_onlyUpdatesStatus() {
        DocumentRequestEntity e = new DocumentRequestEntity();
        e.setOmrdaSourceSystem(ReferenceDataEntity.builder().id(11L).build());
        e.setOmrdaDocumentType(ReferenceDataEntity.builder().id(22L).build());
        e.setOmrdaDocumentName(ReferenceDataEntity.builder().id(33L).build());
        e.setOmrdaDocStatus(ReferenceDataEntity.builder().id(44L).build());

        // Update status to new id; other fields should remain unchanged
        mapper.updateStatus(new DocumentRequestInDto(null, null, null, 55L), e);

        assertThat(e.getOmrdaSourceSystem().getId()).isEqualTo(11L);
        assertThat(e.getOmrdaDocumentType().getId()).isEqualTo(22L);
        assertThat(e.getOmrdaDocumentName().getId()).isEqualTo(33L);
        assertThat(e.getOmrdaDocStatus().getId()).isEqualTo(55L);
    }
}


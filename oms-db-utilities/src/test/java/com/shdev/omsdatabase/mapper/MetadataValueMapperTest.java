package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.MetadataValueInDto;
import com.shdev.omsdatabase.dto.MetadataValueOutDto;
import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import com.shdev.omsdatabase.entity.RequestsMetadataValueEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link MetadataValueMapper} verifying mapping between
 * {@link RequestsMetadataValueEntity} and metadata DTOs.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
@DisplayName("MetadataValueMapper unit tests")
class MetadataValueMapperTest {

    @Autowired
    private MetadataValueMapper mapper;

    @Test
    @DisplayName("toEntity: creates entity with FK references and value")
    void toEntity_createMapping_populatesFKsAndValue() {
        MetadataValueInDto dto = new MetadataValueInDto(101L, 202L, "VAL");
        RequestsMetadataValueEntity e = mapper.toEntity(dto);
        assertThat(e.getOmdrt()).isNotNull();
        assertThat(e.getOmdrt().getId()).isEqualTo(101L);
        assertThat(e.getOmrda()).isNotNull();
        assertThat(e.getOmrda().getId()).isEqualTo(202L);
        assertThat(e.getMetadataValue()).isEqualTo("VAL");
    }

    /**
     * Test: toDto flattens metadata value entity to output DTO
     * Given: RequestsMetadataValueEntity with nested request and metadata key reference data
     * When: toDto is called
     * Then: DTO contains flattened request ID, metadata key details, and value
     */
    @Test
    @DisplayName("toDto: flattens nested metadata key and request ID")
    void toDto_mapsNestedAndRequestId() {
        RequestsMetadataValueEntity e = new RequestsMetadataValueEntity();
        e.setId(9L);
        e.setOmdrt(DocumentRequestEntity.builder().id(88L).build());
        e.setOmrda(ReferenceDataEntity.builder().id(77L).refDataValue("KEY1").description("Key").build());
        e.setMetadataValue("VAL1");

        MetadataValueOutDto dto = mapper.toDto(e);
        assertThat(dto.id()).isEqualTo(9L);
        assertThat(dto.requestId()).isEqualTo(88L);
        assertThat(dto.metadataKey().id()).isEqualTo(77L);
        assertThat(dto.metadataKey().refDataValue()).isEqualTo("KEY1");
        assertThat(dto.metadataValue()).isEqualTo("VAL1");
    }
}

package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.MetadataValueInDto;
import com.shdev.omsdatabase.dto.MetadataValueOutDto;
import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import com.shdev.omsdatabase.entity.RequestsMetadataValueEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
class MetadataValueMapperTest {

    @Autowired
    private MetadataValueMapper mapper;

    @Test
    void toEntity_createMapping_populatesFKsAndValue() {
        MetadataValueInDto dto = new MetadataValueInDto(101L, 202L, "VAL");
        RequestsMetadataValueEntity e = mapper.toEntity(dto);
        assertThat(e.getOmdrt()).isNotNull();
        assertThat(e.getOmdrt().getId()).isEqualTo(101L);
        assertThat(e.getOmrda()).isNotNull();
        assertThat(e.getOmrda().getId()).isEqualTo(202L);
        assertThat(e.getMetadataValue()).isEqualTo("VAL");
    }

    @Test
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


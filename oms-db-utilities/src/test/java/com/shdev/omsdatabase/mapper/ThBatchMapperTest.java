package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.ThBatchInDto;
import com.shdev.omsdatabase.dto.ThBatchOutDto;
import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import com.shdev.omsdatabase.entity.ThBatchEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ThBatchMapper} verifying create, read, and partial update mappings
 * between {@link com.shdev.omsdatabase.entity.ThBatchEntity} and its DTOs.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
@DisplayName("ThBatchMapper unit tests")
class ThBatchMapperTest {

    @Autowired
    private ThBatchMapper mapper;

    @Test
    @DisplayName("toEntity: creates entity from DTO with all fields populated")
    void toEntity_createMapping_populatesAllFields() {
        ThBatchInDto dto = new ThBatchInDto(100L, 200L, 300L, "BATCH-1", 400L, true, false, 5L);
        ThBatchEntity e = mapper.toEntity(dto);
        assertThat(e.getOmdrt()).isNotNull();
        assertThat(e.getOmdrt().getId()).isEqualTo(100L);
        assertThat(e.getThBatchId()).isEqualTo(200L);
        assertThat(e.getOmrdaThStatus()).isNotNull();
        assertThat(e.getOmrdaThStatus().getId()).isEqualTo(300L);
        assertThat(e.getBatchName()).isEqualTo("BATCH-1");
        assertThat(e.getDmsDocumentId()).isEqualTo(400L);
        assertThat(e.getSyncStatus()).isTrue();
        assertThat(e.getEventStatus()).isFalse();
        assertThat(e.getRetryCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("toDto: flattens nested and simple fields to DTO")
    void toDto_mapsNestedAndSimpleFields() {
        ThBatchEntity e = new ThBatchEntity();
        e.setId(9L);
        e.setOmdrt(DocumentRequestEntity.builder().id(777L).build());
        e.setThBatchId(222L);
        e.setOmrdaThStatus(ReferenceDataEntity.builder().id(333L).refDataValue("INPROG").description("In Progress").build());
        e.setBatchName("BATCH-X");
        e.setDmsDocumentId(444L);
        e.setSyncStatus(Boolean.TRUE);
        e.setEventStatus(Boolean.TRUE);
        e.setRetryCount(1L);

        ThBatchOutDto out = mapper.toDto(e);
        assertThat(out.id()).isEqualTo(9L);
        assertThat(out.requestId()).isEqualTo(777L);
        assertThat(out.batchId()).isEqualTo(222L);
        assertThat(out.batchStatus().id()).isEqualTo(333L);
        assertThat(out.batchStatus().refDataValue()).isEqualTo("INPROG");
        assertThat(out.batchName()).isEqualTo("BATCH-X");
        assertThat(out.dmsDocumentId()).isEqualTo(444L);
        assertThat(out.syncStatus()).isTrue();
        assertThat(out.eventStatus()).isTrue();
        assertThat(out.retryCount()).isEqualTo(1L);
    }

    /**
     * Test: updateEntity applies partial update with selective field changes
     * Given: Existing ThBatchEntity and patch DTO with some null and some updated fields
     * When: updateEntity is called
     * Then: Only non-null fields from DTO are updated, null fields are ignored
     */
    @Test
    @DisplayName("updateEntity: applies only non-null fields from patch DTO")
    void updateEntity_partialUpdate_appliesOnlyProvidedFields() {
        ThBatchEntity e = new ThBatchEntity();
        e.setOmdrt(DocumentRequestEntity.builder().id(1L).build());
        e.setThBatchId(10L);
        e.setOmrdaThStatus(ReferenceDataEntity.builder().id(20L).build());
        e.setBatchName("OLD");
        e.setDmsDocumentId(30L);
        e.setSyncStatus(Boolean.FALSE);
        e.setEventStatus(Boolean.FALSE);
        e.setRetryCount(0L);

        ThBatchInDto patch = new ThBatchInDto(null, null, 99L, null, 777L, null, true, 3L);

        mapper.updateEntity(patch, e);

        assertThat(e.getOmdrt().getId()).isEqualTo(1L); // unchanged
        assertThat(e.getThBatchId()).isEqualTo(10L); // unchanged
        assertThat(e.getOmrdaThStatus().getId()).isEqualTo(99L); // updated
        assertThat(e.getBatchName()).isEqualTo("OLD"); // unchanged
        assertThat(e.getDmsDocumentId()).isEqualTo(777L); // updated
        assertThat(e.getSyncStatus()).isFalse(); // unchanged (null ignored)
        assertThat(e.getEventStatus()).isTrue(); // updated
        assertThat(e.getRetryCount()).isEqualTo(3L); // updated
    }
}

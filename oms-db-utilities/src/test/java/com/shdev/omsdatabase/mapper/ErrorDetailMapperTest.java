package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.ErrorDetailDto;
import com.shdev.omsdatabase.entity.ErrorDetailEntity;
import com.shdev.omsdatabase.entity.ThBatchEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ErrorDetailMapper} verifying mapping between
 * {@link ErrorDetailEntity} and {@link ErrorDetailDto}.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MapperTestConfig.class)
@DisplayName("ErrorDetailMapper unit tests")
class ErrorDetailMapperTest {

    @Autowired
    private ErrorDetailMapper mapper;

    @Test
    @DisplayName("toEntity: sets batch reference and error fields from DTO")
    void toEntity_createMapping_setsBatchAndFields() {
        ErrorDetailDto in = new ErrorDetailDto(null, 55L, "VALIDATION_ERROR", "missing field");
        ErrorDetailEntity e = mapper.toEntity(in);
        assertThat(e.getOmtbe()).isNotNull();
        assertThat(e.getOmtbe().getId()).isEqualTo(55L);
        assertThat(e.getErrorCategory()).isEqualTo("VALIDATION_ERROR");
        assertThat(e.getErrorDescription()).isEqualTo("missing field");
    }

    /**
     * Test: toDto flattens ErrorDetailEntity to output DTO
     * Given: ErrorDetailEntity with ID, batch reference, error category, and description
     * When: toDto is called
     * Then: DTO contains error ID, batch ID, category, and description
     */
    @Test
    @DisplayName("toDto: flattens entity to error detail DTO")
    void toDto_mapsIdBatchAndFields() {
        ErrorDetailEntity e = new ErrorDetailEntity();
        e.setId(9L);
        e.setOmtbe(ThBatchEntity.builder().id(66L).build());
        e.setErrorCategory("TIMEOUT");
        e.setErrorDescription("took too long");

        ErrorDetailDto out = mapper.toDto(e);
        assertThat(out.id()).isEqualTo(9L);
        assertThat(out.batchId()).isEqualTo(66L);
        assertThat(out.category()).isEqualTo("TIMEOUT");
        assertThat(out.description()).isEqualTo("took too long");
    }
}

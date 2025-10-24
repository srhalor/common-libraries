package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.constants.RefDataType;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.repository.ReferenceDataRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReferenceDataService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReferenceDataService Unit Tests")
class ReferenceDataServiceTest {

    @InjectMocks private ReferenceDataService service;
    @Mock private ReferenceDataRepository referenceDataRepository;

    /**
     * Test: findByTypeAndName, getByTypeAndNameOrThrow, getIdByTypeAndNameOrThrow,
     * exists, listByType, findMetadataKey, getMetadataKeyIdOrThrow,
     * existsMetadataKey, listMetadataKeys - all return expected results when data exists
     * Given: Existing ReferenceData of type METADATA_KEY and name KEY1
     * When: Various retrieval methods are called
     * Then: All methods return expected results
     */
    @Test
    @DisplayName("findByTypeAndName, getByTypeAndNameOrThrow, getIdByTypeAndNameOrThrow, " +
            "exists, listByType, findMetadataKey, getMetadataKeyIdOrThrow, " +
            "existsMetadataKey, listMetadataKeys: all return expected results when data exists")
    void find_get_exists_list_byTypeAndName() {
        ReferenceDataEntity e = new ReferenceDataEntity();
        e.setId(1L);
        e.setRefDataType(RefDataType.METADATA_KEY.name());
        e.setRefDataName("KEY1");
        when(referenceDataRepository.findByRefDataTypeAndRefDataName("METADATA_KEY", "KEY1"))
                .thenReturn(Optional.of(e));
        when(referenceDataRepository.existsByRefDataTypeAndRefDataName("METADATA_KEY", "KEY1"))
                .thenReturn(true);
        when(referenceDataRepository.findByRefDataType("METADATA_KEY"))
                .thenReturn(List.of(e));

        assertThat(service.findByTypeAndName(RefDataType.METADATA_KEY, "KEY1")).contains(e);
        assertThat(service.getByTypeAndNameOrThrow(RefDataType.METADATA_KEY, "KEY1")).isSameAs(e);
        assertThat(service.getIdByTypeAndNameOrThrow(RefDataType.METADATA_KEY, "KEY1")).isEqualTo(1L);
        assertThat(service.exists(RefDataType.METADATA_KEY, "KEY1")).isTrue();
        assertThat(service.listByType(RefDataType.METADATA_KEY)).hasSize(1);
        assertThat(service.findMetadataKey("KEY1")).contains(e);
        assertThat(service.getMetadataKeyIdOrThrow("KEY1")).isEqualTo(1L);
        assertThat(service.existsMetadataKey("KEY1")).isTrue();
        assertThat(service.listMetadataKeys()).hasSize(1);
    }

    /**
     * Test: getByTypeAndNameOrThrow - throws NotFoundException when data is missing
     * Given: Non-existing ReferenceData of type METADATA_KEY and name MISSING
     * When: getByTypeAndNameOrThrow is called
     * Then: NotFoundException is thrown
     */
    @Test
    @DisplayName("getByTypeAndNameOrThrow: throws NotFoundException when data is missing")
    void getByTypeAndNameOrThrow_notFound() {
        when(referenceDataRepository.findByRefDataTypeAndRefDataName("METADATA_KEY", "MISSING"))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getByTypeAndNameOrThrow(RefDataType.METADATA_KEY, "MISSING"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Reference data not found");
    }
}

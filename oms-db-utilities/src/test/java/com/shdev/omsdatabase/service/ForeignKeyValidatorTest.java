package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.exception.BadRequestException;
import com.shdev.omsdatabase.repository.DocumentRequestRepository;
import com.shdev.omsdatabase.repository.ReferenceDataRepository;
import com.shdev.omsdatabase.repository.ThBatchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ForeignKeyValidator.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ForeignKeyValidator Unit Tests")
class ForeignKeyValidatorTest {

    @Mock ReferenceDataRepository referenceDataRepository;
    @Mock DocumentRequestRepository documentRequestRepository;
    @Mock ThBatchRepository thBatchRepository;

    @InjectMocks ForeignKeyValidator fk;

    /**
     * Test: requireReferenceData - passes when exists
     * Given: Existing ReferenceData ID
     * When: requireReferenceData is called
     * Then: No exception is thrown
     */
    @Test
    @DisplayName("requireReferenceData: passes when exists")
    void referenceData_passes_whenExists() {
        when(referenceDataRepository.existsById(1L)).thenReturn(true);
        assertThatCode(() -> fk.requireReferenceData(1L, "field")).doesNotThrowAnyException();
    }

    /**
     * Test: requireReferenceData - throws when null or missing
     * Given: Null or non-existing ReferenceData ID
     * When: requireReferenceData is called
     * Then: BadRequestException is thrown
     */
    @Test
    @DisplayName("requireReferenceData: throws when null or missing")
    void referenceData_throws_whenNull() {
        assertThatThrownBy(() -> fk.requireReferenceData(null, "field"))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("field");
    }

    /**
     * Test: requireReferenceData - throws when missing
     * Given: Non-existing ReferenceData ID
     * When: requireReferenceData is called
     * Then: BadRequestException is thrown
     */
    @Test
    @DisplayName("requireReferenceData: throws when missing")
    void referenceData_throws_whenMissing() {
        when(referenceDataRepository.existsById(9L)).thenReturn(false);
        assertThatThrownBy(() -> fk.requireReferenceData(9L, "field"))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("field");
    }

    /**
     * Test: requireDocumentRequest - passes when exists
     * Given: Existing DocumentRequest ID
     * When: requireDocumentRequest is called
     * Then: No exception is thrown
     */
    @Test
    @DisplayName("requireDocumentRequest: passes when exists")
    void documentRequest_passes_whenExists() {
        when(documentRequestRepository.existsById(2L)).thenReturn(true);
        assertThatCode(() -> fk.requireDocumentRequest(2L, "req")).doesNotThrowAnyException();
    }

    /**
     * Test: requireDocumentRequest - throws when null or missing
     * Given: Null or non-existing DocumentRequest ID
     * When: requireDocumentRequest is called
     * Then: BadRequestException is thrown
     */
    @Test
    @DisplayName("requireDocumentRequest: throws when null or missing")
    void documentRequest_throws_whenNullOrMissing() {
        assertThatThrownBy(() -> fk.requireDocumentRequest(null, "req"))
            .isInstanceOf(BadRequestException.class);
        when(documentRequestRepository.existsById(8L)).thenReturn(false);
        assertThatThrownBy(() -> fk.requireDocumentRequest(8L, "req"))
            .isInstanceOf(BadRequestException.class);
    }

    /**
     * Test: requireThBatch - passes when exists
     * Given: Existing ThBatch ID
     * When: requireThBatch is called
     * Then: No exception is thrown
     */
    @Test
    @DisplayName("requireThBatch: passes when exists")
    void thBatch_passes_whenExists() {
        when(thBatchRepository.existsById(3L)).thenReturn(true);
        assertThatCode(() -> fk.requireThBatch(3L, "batch")).doesNotThrowAnyException();
    }

    /**
     * Test: requireThBatch - throws when null or missing
     * Given: Null or non-existing ThBatch ID
     * When: requireThBatch is called
     * Then: BadRequestException is thrown
     */
    @Test
    @DisplayName("requireThBatch: throws when null or missing")
    void thBatch_throws_whenNullOrMissing() {
        assertThatThrownBy(() -> fk.requireThBatch(null, "batch"))
            .isInstanceOf(BadRequestException.class);
        when(thBatchRepository.existsById(7L)).thenReturn(false);
        assertThatThrownBy(() -> fk.requireThBatch(7L, "batch"))
            .isInstanceOf(BadRequestException.class);
    }
}

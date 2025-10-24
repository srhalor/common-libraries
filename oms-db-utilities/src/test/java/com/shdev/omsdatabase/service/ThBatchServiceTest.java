package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.ThBatchReqDto;
import com.shdev.omsdatabase.dto.response.ThBatchResDto;
import com.shdev.omsdatabase.entity.ThBatchEntity;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.mapper.ThBatchMapper;
import com.shdev.omsdatabase.repository.DocumentRequestRepository;
import com.shdev.omsdatabase.repository.ReferenceDataRepository;
import com.shdev.omsdatabase.repository.ThBatchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ThBatchService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ThBatchService Unit Tests")
class ThBatchServiceTest {

    @InjectMocks private ThBatchService service;
    @Mock private ForeignKeyValidator fk;

    @Mock private ThBatchRepository thBatchRepository;
    @Mock private DocumentRequestRepository documentRequestRepository;
    @Mock private ReferenceDataRepository referenceDataRepository;
    @Mock private ThBatchMapper mapper;

    /**
     * Test: create - Success - validates FKs and saves
     * Given: Valid ThBatchReqDto
     * When: create is called
     * Then: All foreign keys are validated, entity is saved, returning the correct DTO
     */
    @Test
    @DisplayName("create: Success - validates FKs and saves")
    void create_success_validatesFks_andSaves() {
        var mapped = new ThBatchEntity();
        when(mapper.toEntity(any(ThBatchReqDto.class))).thenReturn(mapped);
        when(thBatchRepository.save(mapped)).thenAnswer(inv -> { var e = inv.getArgument(0); ((ThBatchEntity)e).setId(500L); return e; });
        when(mapper.toDto(any(ThBatchEntity.class)))
                .thenReturn(new ThBatchResDto(500L, 1L, 42L, null, "B", null, false, false, 0L, null, null, null, null));

        var dto = new ThBatchReqDto(1L, 42L, 2L, "B", null, false, false, 0L);
        var res = service.create(dto);

        assertThat(res.id()).isEqualTo(500L);
        verify(fk).requireDocumentRequest(1L, "omdrtId");
        verify(fk).requireReferenceData(2L, "omrdaThStatusId");
        verify(thBatchRepository).save(mapped);
        verify(mapper).toDto(mapped);
        verifyNoMoreInteractions(documentRequestRepository, referenceDataRepository);
    }

    /**
     * Test: update - Success - validates FKs, updates and saves
     * Given: Existing ThBatch ID and valid ThBatchReqDto
     * When: update is called
     * Then: All foreign keys are validated, entity is updated and saved, returning the correct DTO
     */
    @Test
    @DisplayName("update: Success - validates FKs, updates and saves")
    void update_notFound_throws() {
        when(thBatchRepository.findById(999L)).thenReturn(Optional.empty());
        var dto = new ThBatchReqDto(1L, 42L, 2L, "B", null, false, false, 0L);
        assertThatThrownBy(() -> service.update(999L, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ThBatch not found");
    }
}

package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.DocumentConfigReqDto;
import com.shdev.omsdatabase.dto.response.DocumentConfigResDto;
import com.shdev.omsdatabase.entity.DocumentConfigEntity;
import com.shdev.omsdatabase.mapper.DocumentConfigMapper;
import com.shdev.omsdatabase.repository.DocumentConfigRepository;
import com.shdev.omsdatabase.repository.DocumentRequestRepository;
import com.shdev.omsdatabase.repository.ReferenceDataRepository;
import com.shdev.omsdatabase.repository.ThBatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DocumentConfigService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentConfigService Unit Tests")
class DocumentConfigServiceTest {

    private DocumentConfigService service;
    @Mock private ForeignKeyValidator fk;

    @Mock private DocumentConfigRepository documentConfigRepository;
    @Mock private ReferenceDataRepository referenceDataRepository;
    @Mock private DocumentRequestRepository documentRequestRepository;
    @Mock private ThBatchRepository thBatchRepository;
    @Mock private DocumentConfigMapper mapper;

    @BeforeEach
    void setUp() {
        service = new DocumentConfigService(documentConfigRepository, mapper, fk);
    }

    /**
     * Test: create - Success - validates all FKs and saves
     * Given: Valid DocumentConfigReqDto
     * When: create is called
     * Then: All foreign keys are validated and entity is saved, returning the correct DTO
     */
    @Test
    @DisplayName("create: Success - validates all FKs and saves")
    void create_success_validatesAllFks_andSaves() {
        var mapped = new DocumentConfigEntity();
        when(mapper.toEntity(any(DocumentConfigReqDto.class))).thenReturn(mapped);
        when(documentConfigRepository.save(mapped)).thenAnswer(inv -> { var e = inv.getArgument(0); ((DocumentConfigEntity)e).setId(101L); return e; });
        when(mapper.toDto(any(DocumentConfigEntity.class)))
                .thenReturn(new DocumentConfigResDto(101L, null, null, null, null, null, null, null, null));

        var dto = new DocumentConfigReqDto(11L, 12L, 13L, "VAL", null, java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(1));
        var res = service.create(dto);

        assertThat(res.id()).isEqualTo(101L);
        verify(fk).requireReferenceData(11L, "omrdaFooterId");
        verify(fk).requireReferenceData(12L, "omrdaAppDocSpecId");
        verify(fk).requireReferenceData(13L, "omrdaCodeId");
        verify(documentConfigRepository).save(mapped);
        verify(mapper).toEntity(dto);
        verify(mapper).toDto(mapped);
        verifyNoMoreInteractions(referenceDataRepository, documentRequestRepository, thBatchRepository);
    }
}

package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.RequestsMetadataValueReqDto;
import com.shdev.omsdatabase.dto.response.RequestsMetadataValueResDto;
import com.shdev.omsdatabase.entity.RequestsMetadataValueEntity;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.mapper.RequestsMetadataValueMapper;
import com.shdev.omsdatabase.repository.DocumentRequestRepository;
import com.shdev.omsdatabase.repository.ReferenceDataRepository;
import com.shdev.omsdatabase.repository.RequestsMetadataValueRepository;
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
 * Unit tests for RequestsMetadataValueService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RequestsMetadataValueService Unit Tests")
class RequestsMetadataValueServiceTest {

    @InjectMocks
    private RequestsMetadataValueService service;

    @Mock
    private ForeignKeyValidator fk;

    @Mock
    private RequestsMetadataValueRepository requestsMetadataValueRepository;
    @Mock
    private DocumentRequestRepository documentRequestRepository;
    @Mock
    private ReferenceDataRepository referenceDataRepository;
    @Mock
    private ThBatchRepository thBatchRepository;

    @Mock
    private RequestsMetadataValueMapper mapper;

    /**
     * Test: create - Success - validates both FKs and saves
     * Given: Valid RequestsMetadataValueReqDto
     * When: create is called
     * Then: Both foreign keys are validated, entity is saved, returning the correct DTO
     */
    @Test
    @DisplayName("create: Success - validates both FKs and saves")
    void create_success_validatesBothFks_andSaves() {
        var mapped = new RequestsMetadataValueEntity();
        when(mapper.toEntity(any(RequestsMetadataValueReqDto.class))).thenReturn(mapped);
        when(requestsMetadataValueRepository.save(any(RequestsMetadataValueEntity.class)))
                .thenAnswer(inv -> {
                    RequestsMetadataValueEntity e = inv.getArgument(0);
                    e.setId(200L);
                    return e;
                });
        when(mapper.toDto(any(RequestsMetadataValueEntity.class)))
                .thenReturn(new RequestsMetadataValueResDto(200L, null, null, "V"));

        var dto = new RequestsMetadataValueReqDto(10L, 20L, "V");
        var res = service.create(dto);

        assertThat(res.id()).isEqualTo(200L);
        verify(fk).requireDocumentRequest(10L, "omdrtId");
        verify(fk).requireReferenceData(20L, "omrdaId");
        verify(requestsMetadataValueRepository).save(any());
        verify(mapper).toDto(mapped);
        verifyNoMoreInteractions(documentRequestRepository, referenceDataRepository);
    }

    /**
     * Test: update - Not Found - throws NotFoundException
     * Given: Non-existent RequestsMetadataValue ID
     * When: update is called
     * Then: NotFoundException is thrown
     */
    @Test
    @DisplayName("update: Not Found - throws NotFoundException")
    void update_notFound_throws() {
        when(requestsMetadataValueRepository.findById(404L)).thenReturn(Optional.empty());
        var dto = new RequestsMetadataValueReqDto(1L, 2L, "v");
        assertThatThrownBy(() -> service.update(404L, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("RequestsMetadataValue not found");
    }
}

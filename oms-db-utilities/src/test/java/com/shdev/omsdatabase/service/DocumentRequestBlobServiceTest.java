package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.DocumentRequestBlobReqDto;
import com.shdev.omsdatabase.dto.response.DocumentRequestBlobResDto;
import com.shdev.omsdatabase.entity.DocumentRequestBlobEntity;
import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import com.shdev.omsdatabase.exception.BadRequestException;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.mapper.DocumentRequestBlobMapper;
import com.shdev.omsdatabase.repository.DocumentRequestBlobRepository;
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
 * Unit tests for DocumentRequestBlobService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentRequestBlobService Unit Tests")
class DocumentRequestBlobServiceTest {

    @InjectMocks private DocumentRequestBlobService service;
    @Mock private ForeignKeyValidator fk;

    @Mock private DocumentRequestBlobRepository documentRequestBlobRepository;
    @Mock private DocumentRequestRepository documentRequestRepository;
    @Mock private ReferenceDataRepository referenceDataRepository;
    @Mock private ThBatchRepository thBatchRepository;
    @Mock private DocumentRequestBlobMapper mapper;

    /**
     * Test: create - Success - validates DocumentRequest and saves
     * Given: Valid DocumentRequestBlobReqDto
     * When: create is called
     * Then: DocumentRequest FK is validated and entity is saved, returning the correct DTO
     */
    @Test
    @DisplayName("create: Success - validates DocumentRequest and saves")
    void create_success_validatesDocRequest_andSaves() {
        var mapped = new DocumentRequestBlobEntity();
        when(mapper.toEntity(any(DocumentRequestBlobReqDto.class))).thenReturn(mapped);
        when(documentRequestBlobRepository.save(mapped)).thenAnswer(inv -> { var e = inv.getArgument(0); ((DocumentRequestBlobEntity)e).setId(10L); return e; });
        when(mapper.toDto(any(DocumentRequestBlobEntity.class)))
                .thenReturn(new DocumentRequestBlobResDto(10L, null, "{ }", null));

        var dto = new DocumentRequestBlobReqDto(10L, "{ }", null);
        var res = service.create(dto);

        assertThat(res.id()).isEqualTo(10L);
        assertThat(res.jsonRequest()).isEqualTo("{ }");
        verify(fk).requireDocumentRequest(10L, "omdrtId");
        verify(documentRequestBlobRepository).save(mapped);
        verify(mapper).toEntity(dto);
        verify(mapper).toDto(mapped);
        verifyNoMoreInteractions(documentRequestRepository, referenceDataRepository, thBatchRepository);
    }

    /**
     * Test: update - Success - updates existing entity
     * Given: Existing DocumentRequestBlobEntity and valid DocumentRequestBlobReqDto
     * When: update is called
     * Then: Entity is updated and saved, returning the correct DTO
     */
    @Test
    void update_mismatchedId_throwsBadRequest() {
        when(documentRequestBlobRepository.findById(5L)).thenReturn(Optional.of(DocumentRequestBlobEntity.builder()
                .id(5L)
                .tbomDocumentRequests(DocumentRequestEntity.builder().id(5L).build())
                .jsonRequest("{}").build()));

        var dto = new DocumentRequestBlobReqDto(99L, "{}", null);
        assertThatThrownBy(() -> service.update(5L, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("cannot be changed");
    }

    /**
     * Test: update - Not Found - throws NotFoundException
     * Given: Non-existing DocumentRequestBlobEntity
     * When: update is called
     * Then: NotFoundException is thrown
     */
    @Test
    void update_notFound_throws() {
        when(documentRequestBlobRepository.findById(404L)).thenReturn(Optional.empty());
        var dto = new DocumentRequestBlobReqDto(404L, "{}", null);
        assertThatThrownBy(() -> service.update(404L, dto))
                .isInstanceOf(NotFoundException.class);
    }
}

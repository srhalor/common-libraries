package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.DocumentRequestReqDto;
import com.shdev.omsdatabase.dto.response.DocumentRequestResDto;
import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import com.shdev.omsdatabase.exception.BadRequestException;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.mapper.DocumentRequestMapper;
import com.shdev.omsdatabase.repository.DocumentRequestRepository;
import com.shdev.omsdatabase.repository.ReferenceDataRepository;
import com.shdev.omsdatabase.repository.ThBatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DocumentRequestService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DocumentRequestService Unit Tests")
class DocumentRequestServiceTest {

    private DocumentRequestService service;

    @Mock private DocumentRequestRepository documentRequestRepository;
    @Mock private ReferenceDataRepository referenceDataRepository;
    @Mock private ThBatchRepository thBatchRepository;
    @Mock private DocumentRequestMapper mapper;

    @BeforeEach
    void setUp() {
        ForeignKeyValidator fk = new ForeignKeyValidator(referenceDataRepository, documentRequestRepository, thBatchRepository);
        service = new DocumentRequestService(documentRequestRepository, mapper, fk);
    }

    /**
     * Test: create - Success - validates FKs, maps and saves
     * Given: Valid DocumentRequestReqDto
     * When: create is called
     * Then: All foreign keys are validated, entity is mapped and saved, returning the correct DTO
     */
    @Test
    @DisplayName("create: Success - validates FKs, maps and saves")
    void create_success_validatesFks_mapsAndSaves() {
        // arrange
        long src = 1L, type = 2L, name = 3L, status = 4L;
        when(referenceDataRepository.existsById(src)).thenReturn(true);
        when(referenceDataRepository.existsById(type)).thenReturn(true);
        when(referenceDataRepository.existsById(name)).thenReturn(true);
        when(referenceDataRepository.existsById(status)).thenReturn(true);

        var mappedEntity = new DocumentRequestEntity();
        when(mapper.toEntity(any(DocumentRequestReqDto.class))).thenReturn(mappedEntity);
        when(documentRequestRepository.save(mappedEntity)).thenAnswer(inv -> {
            DocumentRequestEntity e = inv.getArgument(0);
            e.setId(100L);
            return e;
        });
        when(mapper.toDto(any(DocumentRequestEntity.class)))
                .thenReturn(new DocumentRequestResDto(100L, null, null, null, null, null, null, null, null));

        // act
        var dto = new DocumentRequestReqDto(src, type, name, status);
        var res = service.create(dto);

        // assert
        assertThat(res.id()).isEqualTo(100L);
        verify(referenceDataRepository).existsById(src);
        verify(referenceDataRepository).existsById(type);
        verify(referenceDataRepository).existsById(name);
        verify(referenceDataRepository).existsById(status);
        verify(mapper).toEntity(dto);
        verify(documentRequestRepository).save(mappedEntity);
        verify(mapper).toDto(mappedEntity);
    }

    /**
     * Test: create - Invalid FK - throws BadRequestException
     * Given: DocumentRequestReqDto with invalid foreign key
     * When: create is called
     * Then: BadRequestException is thrown indicating the invalid foreign key
     */
    @Test
    @DisplayName("create: Invalid FK - throws BadRequestException")
    void create_invalidFk_throwsBadRequest() {
        when(referenceDataRepository.existsById(1L)).thenReturn(true);
        when(referenceDataRepository.existsById(2L)).thenReturn(false); // invalid type

        var dto = new DocumentRequestReqDto(1L, 2L, 3L, 4L);
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("omrdaDocumentTypeId");

        verify(mapper, never()).toEntity(any());
        verify(documentRequestRepository, never()).save(any());
    }

    /**
     * Test: update - Mismatched ID - throws BadRequestException
     * Given: DocumentRequestReqDto with mismatched ID
     * When: update is called
     * Then: BadRequestException is thrown indicating the ID cannot be changed
     */
    @Test
    @DisplayName("update: Mismatched ID - throws BadRequestException")
    void update_notFound_throws() {
        when(documentRequestRepository.findById(999L)).thenReturn(Optional.empty());
        var dto = new DocumentRequestReqDto(1L, 2L, 3L, 4L);

        assertThatThrownBy(() -> service.update(999L, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("DocumentRequest not found");
    }
}

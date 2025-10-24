package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.ErrorDetailReqDto;
import com.shdev.omsdatabase.dto.response.ErrorDetailResDto;
import com.shdev.omsdatabase.entity.ErrorDetailEntity;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.mapper.ErrorDetailMapper;
import com.shdev.omsdatabase.repository.ErrorDetailRepository;
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
 * Unit tests for ErrorDetailService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ErrorDetailService Unit Tests")
class ErrorDetailServiceTest {

    @InjectMocks private ErrorDetailService service;
    @Mock private ForeignKeyValidator fk;

    @Mock private ErrorDetailRepository errorDetailRepository;
    @Mock private ThBatchRepository thBatchRepository;
    @Mock private ErrorDetailMapper mapper;

    /**
     * Test: create - Success - validates ThBatch and saves
     * Given: Valid ErrorDetailReqDto
     * When: create is called
     * Then: ThBatch FK is validated and entity is saved, returning the correct DTO
     */
    @Test
    @DisplayName("create: Success - validates ThBatch and saves")
    void create_success_validatesThBatch_andSaves() {
        var mapped = new ErrorDetailEntity();
        when(mapper.toEntity(any(ErrorDetailReqDto.class))).thenReturn(mapped);
        when(errorDetailRepository.save(mapped)).thenAnswer(inv -> { var e = inv.getArgument(0); ((ErrorDetailEntity)e).setId(700L); return e; });
        when(mapper.toDto(any(ErrorDetailEntity.class)))
                .thenReturn(new ErrorDetailResDto(700L, null, "VALIDATION_ERROR", "oops"));

        var dto = new ErrorDetailReqDto(7L, "VALIDATION_ERROR", "oops");
        var res = service.create(dto);

        assertThat(res.id()).isEqualTo(700L);
        verify(fk).requireThBatch(7L, "omtbeId");
        verify(errorDetailRepository).save(mapped);
        verify(mapper).toDto(mapped);
        verifyNoMoreInteractions(thBatchRepository);
    }

    /**
     * Test: update - Success - finds, updates, saves and returns DTO
     * Given: Existing ErrorDetail ID and valid ErrorDetailReqDto
     * When: update is called
     * Then: Entity is found, updated, saved, and the correct DTO is returned
     */
    @Test
    @DisplayName("update: Success - finds, updates, saves and returns DTO")
    void update_notFound_throws() {
        when(errorDetailRepository.findById(321L)).thenReturn(Optional.empty());
        var dto = new ErrorDetailReqDto(1L, "VALIDATION_ERROR", "oops");
        assertThatThrownBy(() -> service.update(321L, dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ErrorDetail not found");
    }
}

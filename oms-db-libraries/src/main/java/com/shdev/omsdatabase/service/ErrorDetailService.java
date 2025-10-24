package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.ErrorDetailReqDto;
import com.shdev.omsdatabase.dto.response.ErrorDetailResDto;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.mapper.ErrorDetailMapper;
import com.shdev.omsdatabase.repository.ErrorDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for managing TBOM_ERROR_DETAILS lifecycle.
 * Validates foreign keys via ForeignKeyValidator and delegates mapping to MapStruct mappers.
 *
 * @author Shailesh Halor
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ErrorDetailService {

    private final ErrorDetailRepository repository;
    private final ErrorDetailMapper mapper;
    private final ForeignKeyValidator fk;

    /**
     * Create an error detail entry after validating the batch id.
     *
     * @param dto the error detail request DTO
     * @return the created error detail response DTO
     */
    public ErrorDetailResDto create(ErrorDetailReqDto dto) {
        log.debug("Creating ErrorDetail for batchId={} category={}", dto.omtbeId(), dto.errorCategory());

        fk.requireThBatch(dto.omtbeId(), "omtbeId");

        var entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        log.info("Created ErrorDetail id={}", entity.getId());

        return mapper.toDto(entity);
    }

    /**
     * Update an existing error detail entry.
     *
     * @param id  the id of the error detail to update
     * @param dto the error detail request DTO with updated fields
     * @return the updated error detail response DTO
     */
    public ErrorDetailResDto update(Long id, ErrorDetailReqDto dto) {
        log.debug("Updating ErrorDetail id={}", id);
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("ErrorDetail not found: " + id));

        fk.requireThBatch(dto.omtbeId(), "omtbeId");

        mapper.partialUpdate(dto, entity);
        entity = repository.save(entity);
        log.info("Updated ErrorDetail id={}", entity.getId());

        return mapper.toDto(entity);
    }
}

package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.ThBatchReqDto;
import com.shdev.omsdatabase.dto.response.ThBatchResDto;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.mapper.ThBatchMapper;
import com.shdev.omsdatabase.repository.ThBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for managing TBOM_TH_BATCHES lifecycle.
 *
 * @author Shailesh Halor
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ThBatchService {

    private final ThBatchRepository repository;
    private final ThBatchMapper mapper;
    private final ForeignKeyValidator fk;

    /**
     * Create a new Thunderhead batch after FK validation.
     *
     * @param dto the Thunderhead batch request DTO
     * @return the created Thunderhead batch response DTO
     */
    public ThBatchResDto create(ThBatchReqDto dto) {
        log.debug("Creating ThBatch with requestId={}, statusId={}", dto.omdrtId(), dto.omrdaThStatusId());

        fk.requireDocumentRequest(dto.omdrtId(), "omdrtId");
        fk.requireReferenceData(dto.omrdaThStatusId(), "omrdaThStatusId");

        var entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        log.info("Created ThBatch id={}", entity.getId());

        return mapper.toDto(entity);
    }

    /**
     * Update an existing Thunderhead batch after FK validations.
     *
     * @param id  the id of the Thunderhead batch to update
     * @param dto the Thunderhead batch request DTO with updated fields
     * @return the updated Thunderhead batch response DTO
     */
    public ThBatchResDto update(Long id, ThBatchReqDto dto) {
        log.debug("Updating ThBatch id={}", id);
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("ThBatch not found: " + id));

        fk.requireDocumentRequest(dto.omdrtId(), "omdrtId");
        fk.requireReferenceData(dto.omrdaThStatusId(), "omrdaThStatusId");

        mapper.partialUpdate(dto, entity);
        entity = repository.save(entity);
        log.info("Updated ThBatch id={}", entity.getId());

        return mapper.toDto(entity);
    }
}

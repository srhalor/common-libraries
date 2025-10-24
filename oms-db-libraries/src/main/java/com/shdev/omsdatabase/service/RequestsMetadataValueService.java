package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.RequestsMetadataValueReqDto;
import com.shdev.omsdatabase.dto.response.RequestsMetadataValueResDto;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.mapper.RequestsMetadataValueMapper;
import com.shdev.omsdatabase.repository.RequestsMetadataValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for managing TBOM_REQUESTS_METADATA_VALUES lifecycle.
 *
 * @author Shailesh Halor
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RequestsMetadataValueService {

    private final RequestsMetadataValueRepository repository;
    private final RequestsMetadataValueMapper mapper;
    private final ForeignKeyValidator fk;

    /**
     * Create a new metadata value row after validating foreign keys.
     *
     * @param dto the request metadata value request DTO
     * @return the created request metadata value response DTO
     */
    public RequestsMetadataValueResDto create(RequestsMetadataValueReqDto dto) {
        log.debug("Creating RequestsMetadataValue for requestId={} refDataId={}", dto.omdrtId(), dto.omrdaId());

        fk.requireDocumentRequest(dto.omdrtId(), "omdrtId");
        fk.requireReferenceData(dto.omrdaId(), "omrdaId");

        var entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        log.info("Created RequestsMetadataValue id={}", entity.getId());

        return mapper.toDto(entity);
    }

    /**
     * Update an existing metadata value row.
     *
     * @param id  the id of the request metadata value to update
     * @param dto the request metadata value request DTO with updated fields
     * @return the updated request metadata value response DTO
     */
    public RequestsMetadataValueResDto update(Long id, RequestsMetadataValueReqDto dto) {
        log.debug("Updating RequestsMetadataValue id={}", id);
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("RequestsMetadataValue not found: " + id));

        fk.requireDocumentRequest(dto.omdrtId(), "omdrtId");
        fk.requireReferenceData(dto.omrdaId(), "omrdaId");

        mapper.partialUpdate(dto, entity);
        entity = repository.save(entity);
        log.info("Updated RequestsMetadataValue id={}", entity.getId());

        return mapper.toDto(entity);
    }
}

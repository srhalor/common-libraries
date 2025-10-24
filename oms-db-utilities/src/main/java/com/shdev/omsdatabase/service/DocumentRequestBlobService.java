package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.DocumentRequestBlobReqDto;
import com.shdev.omsdatabase.dto.response.DocumentRequestBlobResDto;
import com.shdev.omsdatabase.exception.BadRequestException;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.mapper.DocumentRequestBlobMapper;
import com.shdev.omsdatabase.repository.DocumentRequestBlobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for managing TBOM_DOCUMENT_REQUESTS_BLOB lifecycle.
 *
 * @author Shailesh Halor
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DocumentRequestBlobService {

    private final DocumentRequestBlobRepository repository;
    private final DocumentRequestBlobMapper mapper;
    private final ForeignKeyValidator fk;

    /**
     * Create a new DocumentRequestBlob after validating foreign keys.
     *
     * @param dto the DocumentRequestBlob request DTO
     * @return the created DocumentRequestBlob response DTO
     */
    public DocumentRequestBlobResDto create(DocumentRequestBlobReqDto dto) {
        log.debug("Creating DocumentRequestBlob for requestId={}", dto.omdrtId());

        fk.requireDocumentRequest(dto.omdrtId(), "omdrtId");

        var entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        log.info("Created DocumentRequestBlob id={}", entity.getId());

        return mapper.toDto(entity);
    }

    /**
     * Update an existing DocumentRequestBlob.
     *
     * @param id  the id of the DocumentRequestBlob to update
     * @param dto the DocumentRequestBlob request DTO with updated fields
     * @return the updated DocumentRequestBlob response DTO
     */
    public DocumentRequestBlobResDto update(Long id, DocumentRequestBlobReqDto dto) {
        log.debug("Updating DocumentRequestBlob id={}", id);
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("DocumentRequestBlob not found: " + id));

        if (dto.omdrtId() != null && !dto.omdrtId().equals(id)) {
            throw new BadRequestException("omdrtId cannot be changed on update (path id=" + id + ")");
        }

        mapper.partialUpdate(dto, entity);
        entity = repository.save(entity);
        log.info("Updated DocumentRequestBlob id={}", entity.getId());

        return mapper.toDto(entity);
    }
}

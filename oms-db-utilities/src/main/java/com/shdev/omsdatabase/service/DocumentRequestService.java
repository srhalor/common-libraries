package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.DocumentRequestReqDto;
import com.shdev.omsdatabase.dto.response.DocumentRequestResDto;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.mapper.DocumentRequestMapper;
import com.shdev.omsdatabase.repository.DocumentRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for managing TBOM_DOCUMENT_REQUESTS lifecycle.
 * Validates foreign keys via ForeignKeyValidator and delegates mapping to MapStruct mappers.
 * <p>
 * * @author Shailesh Halor
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DocumentRequestService {

    private final DocumentRequestRepository repository;
    private final DocumentRequestMapper mapper;
    private final ForeignKeyValidator fk;

    /**
     * Create a new document request after FK validation.
     *
     * @param dto the document request request DTO
     * @return the created document request response DTO
     */
    public DocumentRequestResDto create(DocumentRequestReqDto dto) {
        log.debug("Creating DocumentRequest with sourceSystemId={}, documentTypeId={}, documentNameId={}, statusId={}",
                dto.omrdaSourceSystemId(), dto.omrdaDocumentTypeId(), dto.omrdaDocumentNameId(), dto.omrdaDocStatusId());

        // Perform FK validations
        validateFk(dto);

        var entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        log.info("Created DocumentRequest id={}", entity.getId());

        return mapper.toDto(entity);
    }

    /**
     * Update an existing document request after FK validations.
     *
     * @param id  the id of the document request to update
     * @param dto the document request DTO with updated fields
     * @return the updated document request response DTO
     */
    public DocumentRequestResDto update(Long id, DocumentRequestReqDto dto) {
        log.debug("Updating DocumentRequest id={}", id);
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("DocumentRequest not found: " + id));

        // Perform FK validations
        validateFk(dto);

        mapper.partialUpdate(dto, entity);
        entity = repository.save(entity);
        log.info("Updated DocumentRequest id={}", entity.getId());

        return mapper.toDto(entity);
    }

    /**
     * Validate foreign key references in the DTO.
     *
     * @param dto the document request request DTO
     */
    private void validateFk(DocumentRequestReqDto dto) {
        log.trace("Validating FKs for DocumentRequest sourceSystemId={}, documentTypeId={}, documentNameId={}, statusId={}",
                dto.omrdaSourceSystemId(), dto.omrdaDocumentTypeId(), dto.omrdaDocumentNameId(), dto.omrdaDocStatusId());
        fk.requireReferenceData(dto.omrdaSourceSystemId(), "omrdaSourceSystemId");
        fk.requireReferenceData(dto.omrdaDocumentTypeId(), "omrdaDocumentTypeId");
        fk.requireReferenceData(dto.omrdaDocumentNameId(), "omrdaDocumentNameId");
        fk.requireReferenceData(dto.omrdaDocStatusId(), "omrdaDocStatusId");
    }
}

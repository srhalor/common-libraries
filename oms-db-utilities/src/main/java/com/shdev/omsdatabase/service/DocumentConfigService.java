package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.DocumentConfigReqDto;
import com.shdev.omsdatabase.dto.response.DocumentConfigResDto;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.mapper.DocumentConfigMapper;
import com.shdev.omsdatabase.repository.DocumentConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for managing TBOM_DOCUMENT_CONFIGURATIONS lifecycle.
 *
 * @author Shailesh Halor
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DocumentConfigService {

    private final DocumentConfigRepository repository;
    private final DocumentConfigMapper mapper;
    private final ForeignKeyValidator fk;

    /**
     * Create a new document configuration after FK validations.
     *
     * @param dto the document configuration request DTO
     * @return the created document configuration response DTO
     */
    public DocumentConfigResDto create(DocumentConfigReqDto dto) {
        log.debug("Creating DocumentConfig footerId={}, appDocSpecId={}, codeId={}",
                dto.omrdaFooterId(), dto.omrdaAppDocSpecId(), dto.omrdaCodeId());

        // Perform FK validations
        validateFk(dto);

        var entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        log.info("Created DocumentConfig id={}", entity.getId());

        return mapper.toDto(entity);
    }

    /**
     * Update an existing document configuration after FK validations.
     *
     * @param id  the id of the document configuration to update
     * @param dto the document configuration request DTO with updated fields
     * @return the updated document configuration response DTO
     */
    public DocumentConfigResDto update(Long id, DocumentConfigReqDto dto) {
        log.debug("Updating DocumentConfig id={}", id);
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("DocumentConfig not found: " + id));

        // Perform FK validations
        validateFk(dto);

        mapper.partialUpdate(dto, entity);
        entity = repository.save(entity);
        log.info("Updated DocumentConfig id={}", entity.getId());

        return mapper.toDto(entity);
    }

    /**
     * Validate foreign key references in the DTO.
     *
     * @param dto the document configuration request DTO
     */
    private void validateFk(DocumentConfigReqDto dto) {
        log.trace("Validating FKs for DocumentConfig footerId={}, appDocSpecId={}, codeId={}",
                dto.omrdaFooterId(), dto.omrdaAppDocSpecId(), dto.omrdaCodeId());
        fk.requireReferenceData(dto.omrdaFooterId(), "omrdaFooterId");
        fk.requireReferenceData(dto.omrdaAppDocSpecId(), "omrdaAppDocSpecId");
        fk.requireReferenceData(dto.omrdaCodeId(), "omrdaCodeId");
    }
}

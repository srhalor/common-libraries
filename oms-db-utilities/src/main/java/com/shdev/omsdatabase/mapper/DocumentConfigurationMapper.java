package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.DocumentConfigInDto;
import com.shdev.omsdatabase.dto.DocumentConfigOutDto;
import com.shdev.omsdatabase.entity.DocumentConfigEntity;
import org.mapstruct.*;

/**
 * Mapper interface for DocumentConfigEntity and DTOs.
 * Handles:
 * - Entity to Response DTO conversion (for retrieval)
 * - Request DTO to Entity conversion (for create)
 * - Partial Request DTO to Entity update (for update with only provided fields)
 *
 * @author Shailesh Halor
 */
@Mapper(componentModel = "spring", uses = ReferenceDataMapper.class)
public interface DocumentConfigurationMapper {

    /**
     * Convert entity to response DTO for retrieval operations.
     * Maps reference data entities to lite DTOs using ReferenceDataMapper.
     *
     * @param entity the document configuration entity
     * @return the document configuration response DTO
     */
    @Mapping(target = "footer", source = "omrdaFooter")
    @Mapping(target = "appDocSpec", source = "omrdaAppDocSpec")
    @Mapping(target = "code", source = "omrdaCode")
    @Mapping(target = "desc", source = "description")
    DocumentConfigOutDto toDto(DocumentConfigEntity entity);

    /**
     * Convert request DTO to entity for create operations.
     * Creates new entity with reference data entities built from IDs.
     * Audit fields (createdDat, createUid, etc.) are handled by JPA lifecycle listeners.
     *
     * @param dto the document configuration request DTO
     * @return a new document configuration entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "omrdaFooter", source = "footerId", qualifiedByName = "idToRefEntity")
    @Mapping(target = "omrdaAppDocSpec", source = "appDocSpecId", qualifiedByName = "idToRefEntity")
    @Mapping(target = "omrdaCode", source = "codeId", qualifiedByName = "idToRefEntity")
    DocumentConfigEntity toEntity(DocumentConfigInDto dto);

    /**
     * Partially update an existing entity from request DTO for update operations.
     * Only non-null fields from the DTO are applied to the entity.
     * Audit fields (lastUpdateDat, lastUpdateUid) are handled by JPA lifecycle listeners.
     *
     * @param dto    the document configuration request DTO with fields to update
     * @param entity the existing entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "omrdaFooter", source = "footerId", qualifiedByName = "idToRefEntity")
    @Mapping(target = "omrdaAppDocSpec", source = "appDocSpecId", qualifiedByName = "idToRefEntity")
    @Mapping(target = "omrdaCode", source = "codeId", qualifiedByName = "idToRefEntity")
    void updateEntity(DocumentConfigInDto dto, @MappingTarget DocumentConfigEntity entity);
}

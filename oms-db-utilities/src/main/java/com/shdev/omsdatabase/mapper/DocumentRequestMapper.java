package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.DocumentRequestInDto;
import com.shdev.omsdatabase.dto.DocumentRequestOutDto;
import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import org.mapstruct.*;

/**
 * Mapper interface for DocumentRequestEntity and DTOs.
 * Handles:
 * - Entity to Response DTO conversion (for retrieval)
 * - Request DTO to Entity conversion (for create)
 * - Partial Request DTO to Entity update (for updating status only)
 *
 * @author Shailesh Halor
 */
@Mapper(componentModel = "spring", uses = ReferenceDataMapper.class)
public interface DocumentRequestMapper {

    /**
     * Convert entity to response DTO for retrieval operations.
     * Maps reference data entities to lite DTOs using ReferenceDataMapper.
     *
     * @param e the document request entity
     * @return the document request response DTO
     */
    @Mapping(target = "sourceSystem", source = "omrdaSourceSystem")
    @Mapping(target = "documentType", source = "omrdaDocumentType")
    @Mapping(target = "documentName", source = "omrdaDocumentName")
    @Mapping(target = "documentStatus", source = "omrdaDocStatus")
    DocumentRequestOutDto toDto(DocumentRequestEntity e);

    /**
     * Convert request DTO to entity for create operations.
     * Creates new entity with reference data entities built from IDs.
     * Audit fields (createdDat, createUid, etc.) are handled by JPA lifecycle listeners.
     *
     * @param dto the document request request DTO
     * @return a new document request entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "omrdaSourceSystem", source = "sourceSystemId", qualifiedByName = "idToRefEntity")
    @Mapping(target = "omrdaDocumentType", source = "documentTypeId", qualifiedByName = "idToRefEntity")
    @Mapping(target = "omrdaDocumentName", source = "documentNameId", qualifiedByName = "idToRefEntity")
    @Mapping(target = "omrdaDocStatus", source = "docStatusId", qualifiedByName = "idToRefEntity")
    DocumentRequestEntity toEntity(DocumentRequestInDto dto);

    /**
     * Partially update an existing entity from request DTO for updating status only.
     * Only the docStatusId from the DTO is applied to the entity.
     * Audit fields (lastUpdateDat, lastUpdateUid) are handled by JPA lifecycle listeners.
     *
     * @param dto    the document request request DTO with status to update
     * @param entity the existing entity to update
     */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "omrdaDocStatus", source = "docStatusId", qualifiedByName = "idToRefEntity")
    void updateStatus(DocumentRequestInDto dto, @MappingTarget DocumentRequestEntity entity);

    /**
     * Helper method to convert an ID to a DocumentRequestEntity with only the ID set.
     *
     * @param id the document request ID
     * @return a DocumentRequestEntity with the given ID
     */
    @Named("idToRequestEntity")
    default DocumentRequestEntity idToRequestEntity(Long id) {
        if (id == null) return null;
        return DocumentRequestEntity.builder().id(id).build();
    }
}

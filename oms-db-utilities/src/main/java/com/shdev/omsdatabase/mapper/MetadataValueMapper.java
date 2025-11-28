package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.MetadataValueInDto;
import com.shdev.omsdatabase.dto.MetadataValueOutDto;
import com.shdev.omsdatabase.entity.RequestsMetadataValueEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for RequestsMetadataValueEntity and DTOs.
 * Handles:
 * - Entity to DTO conversion (for retrieval)
 * - DTO to Entity conversion (for create)
 *
 * @author Shailesh Halor
 */
@Mapper(componentModel = "spring", uses = {ReferenceDataMapper.class, DocumentRequestMapper.class})
public interface MetadataValueMapper {

    /**
     * Convert entity to DTO for retrieval operations.
     *
     * @param entity the requests metadata value entity
     * @return the metadata value DTO
     */
    @Mapping(target = "requestId", source = "omdrt.id")
    @Mapping(target = "metadataKey", source = "omrda")
    MetadataValueOutDto toDto(RequestsMetadataValueEntity entity);

    /**
     * Convert DTO to entity for create operations.
     * Creates new entity with associated request and reference data entities built from IDs.
     * Audit fields (createdDat, createUid, etc.) are handled by JPA lifecycle listeners.
     *
     * @param dto the metadata value DTO
     * @return a new requests metadata value entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "omdrt", source = "requestId", qualifiedByName = "idToRequestEntity")
    @Mapping(target = "omrda", source = "metadataKeyId", qualifiedByName = "idToRefEntity")
    RequestsMetadataValueEntity toEntity(MetadataValueInDto dto);
}

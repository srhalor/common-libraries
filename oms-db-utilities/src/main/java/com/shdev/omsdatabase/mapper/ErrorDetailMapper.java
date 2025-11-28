package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.ErrorDetailDto;
import com.shdev.omsdatabase.entity.ErrorDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for ErrorDetailEntity and ErrorDetailDto.
 * Handles:
 * - Entity to DTO conversion (for retrieval)
 * - DTO to Entity conversion (for create)
 *
 * @author Shailesh Halor
 */
@Mapper(componentModel = "spring", uses = {ThBatchMapper.class})
public interface ErrorDetailMapper {

    /**
     * Convert entity to DTO for retrieval operations.
     *
     * @param e the error detail entity
     * @return the error detail DTO
     */
    @Mapping(target = "batchId", source = "omtbe.id")
    @Mapping(target = "category", source = "errorCategory")
    @Mapping(target = "description", source = "errorDescription")
    ErrorDetailDto toDto(ErrorDetailEntity e);

    /**
     * Convert DTO to entity for create operations.
     * Creates new entity with associated batch entity built from ID.
     * Audit fields (createdDat, createUid, etc.) are handled by JPA lifecycle listeners.
     *
     * @param dto the error detail DTO
     * @return a new error detail entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "omtbe", source = "batchId", qualifiedByName = "idToThBatchEntity")
    @Mapping(target = "errorCategory", source = "category")
    @Mapping(target = "errorDescription", source = "description")
    ErrorDetailEntity toEntity(ErrorDetailDto dto);
}

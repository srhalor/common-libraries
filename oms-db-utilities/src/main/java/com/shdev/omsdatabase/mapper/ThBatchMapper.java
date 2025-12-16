package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.ThBatchInDto;
import com.shdev.omsdatabase.dto.ThBatchOutDto;
import com.shdev.omsdatabase.entity.ThBatchEntity;
import org.mapstruct.*;

/**
 * Mapper interface for ThBatchEntity and DTOs.
 * Handles:
 * - Entity to DTO conversion (for retrieval)
 * - DTO to Entity conversion (for create and update)
 *
 * @author Shailesh Halor
 */
@Mapper(
        componentModel = "spring",
        uses = {ReferenceDataMapper.class, DocumentRequestMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ThBatchMapper {

    /**
     * Convert entity to DTO for retrieval operations.
     *
     * @param entity the TH batch entity
     * @return the TH batch DTO
     */
    @Mapping(target = "requestId", source = "omdrt.id")
    @Mapping(target = "batchId", source = "thBatchId")
    @Mapping(target = "batchStatus", source = "omrdaThStatus")
    ThBatchOutDto toDto(ThBatchEntity entity);

    /**
     * Convert DTO to entity for create operations.
     * Creates new entity with associated request and reference data entities built from IDs.
     * Audit fields are handled by JPA lifecycle listeners.
     *
     * @param dto the TH batch DTO
     * @return a new TH batch entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "omdrt", source = "requestId", qualifiedByName = "idToRequestEntity")
    @Mapping(target = "thBatchId", source = "batchId")
    @Mapping(target = "omrdaThStatus", source = "batchStatusId", qualifiedByName = "idToRefEntity")
    ThBatchEntity toEntity(ThBatchInDto dto);

    /**
     * Partially update an existing entity from DTO for update operations.
     * Only non-null fields from the DTO are applied to the entity.
     * Audit fields are handled by JPA lifecycle listeners.
     *
     * @param dto    the TH batch DTO with fields to update
     * @param entity the existing entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "omdrt", ignore = true)
    @Mapping(target = "thBatchId", source = "batchId")
    @Mapping(target = "omrdaThStatus", source = "batchStatusId", qualifiedByName = "idToRefEntity")
    void updateEntity(ThBatchInDto dto, @MappingTarget ThBatchEntity entity);

    /**
     * Helper method to build ThBatchEntity from ID.
     *
     * @param id the TH batch ID
     * @return a ThBatchEntity with only the ID set, or null if ID is null
     */
    @Named("idToThBatchEntity")
    default ThBatchEntity idToThBatchEntity(Long id) {
        if (id == null) return null;
        return ThBatchEntity.builder().id(id).build();
    }
}

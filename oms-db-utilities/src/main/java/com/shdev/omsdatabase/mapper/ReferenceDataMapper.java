package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.ReferenceDataDto;
import com.shdev.omsdatabase.dto.ReferenceDataLiteDto;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import com.shdev.omsdatabase.util.DateUtils;
import org.mapstruct.*;

import java.time.LocalDate;

/**
 * Mapper interface for ReferenceDataEntity and its DTOs.
 * Handles:
 * - Entity to DTO conversion
 * - DTO to Entity conversion
 * - Partial updates from DTO to existing Entity
 * - Lite DTO mapping for nested usage
 *
 * @author Shailesh Halor
 */
@Mapper(componentModel = "spring")
public interface ReferenceDataMapper {

    /**
     * Mapping from Entity to DTO
     *
     * @param entity the ReferenceDataEntity
     * @return the mapped ReferenceDataDto
     */
    ReferenceDataDto toDto(ReferenceDataEntity entity);

    /**
     * Mapping from DTO to Entity
     *
     * @param dto the ReferenceDataDto
     * @return the mapped ReferenceDataEntity
     */
    @InheritInverseConfiguration
    ReferenceDataEntity toEntity(ReferenceDataDto dto);

    /**
     * Partial update of existing Entity from DTO
     * Only non-null fields from DTO are applied to Entity
     *
     * @param dto    the ReferenceDataDto with fields to update
     * @param entity the existing ReferenceDataEntity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(ReferenceDataDto dto, @MappingTarget ReferenceDataEntity entity);

    /**
     * Apply default values after mapping from DTO to Entity
     * Sets effectFromDat to current date if null
     * Sets effectToDat to Oracle end date if null
     *
     * @param entity the ReferenceDataEntity being mapped
     */
    @AfterMapping
    default void applyDefaults(@MappingTarget ReferenceDataEntity entity) {
        if (entity.getEffectFromDat() == null) {
            entity.setEffectFromDat(LocalDate.now());
        }
        if (entity.getEffectToDat() == null) {
            entity.setEffectToDat(DateUtils.oracleEndDate());
        }
    }

    /**
     * Mapping to lightweight DTO for nested usage
     *
     * @param entity the ReferenceDataEntity
     * @return the mapped ReferenceDataLiteDto
     */
    ReferenceDataLiteDto toLite(ReferenceDataEntity entity);

    /**
     * Convert Long id to ReferenceDataEntity with only id set
     *
     * @param id the ReferenceDataEntity id
     * @return ReferenceDataEntity with id set or null if id is null
     */
    @Named("idToRefEntity")
    default ReferenceDataEntity idToRefEntity(Long id) {
        if (id == null) {
            return null; // allows NullValuePropertyMappingStrategy.IGNORE in consumers
        }
        return ReferenceDataEntity.builder()
                .id(id)
                .build();
    }
}

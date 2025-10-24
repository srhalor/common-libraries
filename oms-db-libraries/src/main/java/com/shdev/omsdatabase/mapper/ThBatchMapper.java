package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.request.ThBatchReqDto;
import com.shdev.omsdatabase.dto.response.ThBatchResDto;
import com.shdev.omsdatabase.entity.ThBatchEntity;
import org.mapstruct.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Mapper for the entity {@link ThBatchEntity} and its DTOs.
 *
 * @author Shailesh Halor
 */
@Mapper(unmappedTargetPolicy = IGNORE, componentModel = SPRING, uses = {DocumentRequestMapper.class, ReferenceDataMapper.class, EntityIdMapper.class})
public interface ThBatchMapper {

    /**
     * Converts a ThBatchReqDto to a ThBatchEntity.
     * Maps FK ids to lightweight entity references.
     *
     * @param dto the request DTO received from API layer
     * @return the mapped entity for persistence
     */
    @Mapping(target = "omdrt", source = "omdrtId")
    @Mapping(target = "omrdaThStatus", source = "omrdaThStatusId")
    ThBatchEntity toEntity(ThBatchReqDto dto);

    /**
     * Partially updates a ThBatchEntity with values from a ThBatchReqDto.
     * Null values in the DTO are ignored.
     *
     * @param dto    the DTO containing fields to update
     * @param entity the existing entity to be updated in place
     * @return the updated entity reference (same instance)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "omdrt", source = "omdrtId")
    @Mapping(target = "omrdaThStatus", source = "omrdaThStatusId")
    ThBatchEntity partialUpdate(ThBatchReqDto dto, @MappingTarget ThBatchEntity entity);

    /**
     * Converts a ThBatchEntity to a ThBatchResDto.
     *
     * @param entity the entity loaded from persistence
     * @return the response DTO suitable for serialization
     */
    ThBatchResDto toDto(ThBatchEntity entity);
}

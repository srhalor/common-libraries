package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.request.ErrorDetailReqDto;
import com.shdev.omsdatabase.dto.response.ErrorDetailResDto;
import com.shdev.omsdatabase.entity.ErrorDetailEntity;
import org.mapstruct.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Mapper for the entity {@link ErrorDetailEntity} and its DTOs.
 *
 * @author Shailesh Halor
 */
@Mapper(unmappedTargetPolicy = IGNORE, componentModel = SPRING, uses = {ThBatchMapper.class, EntityIdMapper.class})
public interface ErrorDetailMapper {

    /**
     * Converts an ErrorDetailReqDto to an ErrorDetailEntity.
     * Maps the omtbeId to the ThBatchEntity association.
     *
     * @param dto the request DTO received from API layer
     * @return the mapped entity for persistence
     */
    @Mapping(target = "omtbe", source = "omtbeId")
    ErrorDetailEntity toEntity(ErrorDetailReqDto dto);

    /**
     * Partially updates an ErrorDetailEntity with values from an ErrorDetailReqDto.
     * Null values in the DTO are ignored.
     *
     * @param dto    the DTO containing fields to update
     * @param entity the existing entity to be updated in place
     * @return the updated entity reference (same instance)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "omtbe", source = "omtbeId")
    ErrorDetailEntity partialUpdate(ErrorDetailReqDto dto, @MappingTarget ErrorDetailEntity entity);

    /**
     * Converts an ErrorDetailEntity to an ErrorDetailResDto.
     *
     * @param entity the entity loaded from persistence
     * @return the response DTO suitable for serialization
     */
    ErrorDetailResDto toDto(ErrorDetailEntity entity);
}

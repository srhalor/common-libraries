package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.request.ReferenceDataReqDto;
import com.shdev.omsdatabase.dto.response.ReferenceDataResDto;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Mapper for the entity {@link ReferenceDataEntity} and its DTOs.
 *
 * @author Shailesh Halor
 */
@Mapper(unmappedTargetPolicy = IGNORE, componentModel = SPRING)
public interface ReferenceDataMapper {

    /**
     * Converts a ReferenceDataReqDto to a ReferenceDataEntity.
     *
     * @param referenceDataReqDto the DTO to convert
     * @return the converted entity
     */
    ReferenceDataEntity toEntity(ReferenceDataReqDto referenceDataReqDto);

    /**
     * Partially updates a ReferenceDataEntity with values from a ReferenceDataReqDto.
     * Null values in the DTO are ignored.
     *
     * @param referenceDataReqDto the DTO containing updated values
     * @param referenceDataEntity the entity to update
     * @return the updated entity
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ReferenceDataEntity partialUpdate(ReferenceDataReqDto referenceDataReqDto, @MappingTarget ReferenceDataEntity referenceDataEntity);

    /**
     * Converts a ReferenceDataEntity to a ReferenceDataResDto.
     *
     * @param referenceDataEntity the entity to convert
     * @return the converted DTO
     */
    ReferenceDataResDto toDto(ReferenceDataEntity referenceDataEntity);

}

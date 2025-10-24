package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.request.RequestsMetadataValueReqDto;
import com.shdev.omsdatabase.dto.response.RequestsMetadataValueResDto;
import com.shdev.omsdatabase.entity.RequestsMetadataValueEntity;
import org.mapstruct.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Mapper for the entity {@link RequestsMetadataValueEntity} and its DTOs.
 *
 * @author Shailesh Halor
 */
@Mapper(unmappedTargetPolicy = IGNORE, componentModel = SPRING, uses = {DocumentRequestMapper.class, ReferenceDataMapper.class, EntityIdMapper.class})
public interface RequestsMetadataValueMapper {

    /**
     * Converts a RequestsMetadataValueReqDto to a RequestsMetadataValueEntity.
     * Maps FK ids to lightweight entity references.
     *
     * @param dto the request DTO received from API layer
     * @return the mapped entity for persistence
     */
    @Mapping(target = "omdrt", source = "omdrtId")
    @Mapping(target = "omrda", source = "omrdaId")
    RequestsMetadataValueEntity toEntity(RequestsMetadataValueReqDto dto);

    /**
     * Partially updates a RequestsMetadataValueEntity with values from a RequestsMetadataValueReqDto.
     * Null values in the DTO are ignored.
     *
     * @param dto    the DTO containing fields to update
     * @param entity the existing entity to be updated in place
     * @return the updated entity reference (same instance)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "omdrt", source = "omdrtId")
    @Mapping(target = "omrda", source = "omrdaId")
    RequestsMetadataValueEntity partialUpdate(RequestsMetadataValueReqDto dto, @MappingTarget RequestsMetadataValueEntity entity);

    /**
     * Converts a RequestsMetadataValueEntity to a RequestsMetadataValueResDto.
     *
     * @param entity the entity loaded from persistence
     * @return the response DTO suitable for serialization
     */
    RequestsMetadataValueResDto toDto(RequestsMetadataValueEntity entity);
}

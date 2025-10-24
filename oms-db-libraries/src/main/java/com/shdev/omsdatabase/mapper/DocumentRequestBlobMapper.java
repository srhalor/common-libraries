package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.request.DocumentRequestBlobReqDto;
import com.shdev.omsdatabase.dto.response.DocumentRequestBlobResDto;
import com.shdev.omsdatabase.entity.DocumentRequestBlobEntity;
import org.mapstruct.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Mapper for the entity {@link DocumentRequestBlobEntity} and its DTOs.
 *
 * @author Shailesh Halor
 */
@Mapper(unmappedTargetPolicy = IGNORE, componentModel = SPRING, uses = {DocumentRequestMapper.class, EntityIdMapper.class})
public interface DocumentRequestBlobMapper {

    /**
     * Converts a DocumentRequestBlobReqDto to a DocumentRequestBlobEntity.
     * Maps omdrtId to the tbomDocumentRequests association (and via @MapsId sets id).
     *
     * @param dto the request DTO received from API layer
     * @return the mapped entity for persistence
     */
    @Mapping(target = "tbomDocumentRequests", source = "omdrtId")
    DocumentRequestBlobEntity toEntity(DocumentRequestBlobReqDto dto);

    /**
     * Partially updates a DocumentRequestBlobEntity with values from a DocumentRequestBlobReqDto.
     * Null values in the DTO are ignored.
     *
     * @param dto    the DTO containing fields to update
     * @param entity the existing entity to be updated in place
     * @return the updated entity reference (same instance)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tbomDocumentRequests", source = "omdrtId")
    DocumentRequestBlobEntity partialUpdate(DocumentRequestBlobReqDto dto, @MappingTarget DocumentRequestBlobEntity entity);

    /**
     * Converts a DocumentRequestBlobEntity to a DocumentRequestBlobResDto.
     *
     * @param entity the entity loaded from persistence
     * @return the response DTO suitable for serialization
     */
    DocumentRequestBlobResDto toDto(DocumentRequestBlobEntity entity);
}

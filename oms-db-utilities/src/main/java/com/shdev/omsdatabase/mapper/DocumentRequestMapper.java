package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.request.DocumentRequestReqDto;
import com.shdev.omsdatabase.dto.response.DocumentRequestResDto;
import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import org.mapstruct.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Mapper for the entity {@link DocumentRequestEntity} and its DTOs.
 *
 * @author Shailesh Halor
 */
@Mapper(unmappedTargetPolicy = IGNORE, componentModel = SPRING, uses = {ReferenceDataMapper.class, EntityIdMapper.class})
public interface DocumentRequestMapper {

    /**
     * Converts a DocumentRequestReqDto to a DocumentRequestEntity.
     * Maps FK ids to lightweight entity references.
     *
     * @param documentRequestReqDto the request DTO received from API layer
     * @return the mapped entity for persistence
     */
    @Mapping(target = "omrdaSourceSystem", source = "omrdaSourceSystemId")
    @Mapping(target = "omrdaDocumentType", source = "omrdaDocumentTypeId")
    @Mapping(target = "omrdaDocumentName", source = "omrdaDocumentNameId")
    @Mapping(target = "omrdaDocStatus", source = "omrdaDocStatusId")
    DocumentRequestEntity toEntity(DocumentRequestReqDto documentRequestReqDto);

    /**
     * Partially updates a DocumentRequestEntity with values from a DocumentRequestReqDto.
     * Null values in the DTO are ignored.
     *
     * @param documentRequestReqDto the DTO containing fields to update
     * @param documentRequestEntity the existing entity to be updated in place
     * @return the updated entity reference (same instance)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "omrdaSourceSystem", source = "omrdaSourceSystemId")
    @Mapping(target = "omrdaDocumentType", source = "omrdaDocumentTypeId")
    @Mapping(target = "omrdaDocumentName", source = "omrdaDocumentNameId")
    @Mapping(target = "omrdaDocStatus", source = "omrdaDocStatusId")
    DocumentRequestEntity partialUpdate(DocumentRequestReqDto documentRequestReqDto, @MappingTarget DocumentRequestEntity documentRequestEntity);

    /**
     * Converts a DocumentRequestEntity to a DocumentRequestResDto.
     *
     * @param documentRequestEntity the entity loaded from persistence
     * @return the response DTO suitable for serialization
     */
    DocumentRequestResDto toDto(DocumentRequestEntity documentRequestEntity);

}
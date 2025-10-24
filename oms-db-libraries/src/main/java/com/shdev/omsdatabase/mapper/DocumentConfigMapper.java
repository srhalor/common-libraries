package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.dto.request.DocumentConfigReqDto;
import com.shdev.omsdatabase.dto.response.DocumentConfigResDto;
import com.shdev.omsdatabase.entity.DocumentConfigEntity;
import org.mapstruct.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

/**
 * Mapper for the entity {@link DocumentConfigEntity} and its DTOs.
 *
 * @author Shailesh Halor
 */
@Mapper(unmappedTargetPolicy = IGNORE, componentModel = SPRING, uses = {ReferenceDataMapper.class, EntityIdMapper.class})
public interface DocumentConfigMapper {

    /**
     * Converts a DocumentConfigReqDto to a DocumentConfigEntity.
     * Maps FK ids to lightweight entity references.
     *
     * @param documentConfigReqDto the request DTO received from API layer
     * @return the mapped entity for persistence
     */
    @Mapping(target = "omrdaFooter", source = "omrdaFooterId")
    @Mapping(target = "omrdaAppDocSpec", source = "omrdaAppDocSpecId")
    @Mapping(target = "omrdaCode", source = "omrdaCodeId")
    DocumentConfigEntity toEntity(DocumentConfigReqDto documentConfigReqDto);

    /**
     * Partially updates a DocumentConfigEntity with values from a DocumentConfigReqDto.
     * Null values in the DTO are ignored.
     *
     * @param documentConfigReqDto the DTO containing fields to update
     * @param documentConfigEntity the existing entity to be updated in place
     * @return the updated entity reference (same instance)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "omrdaFooter", source = "omrdaFooterId")
    @Mapping(target = "omrdaAppDocSpec", source = "omrdaAppDocSpecId")
    @Mapping(target = "omrdaCode", source = "omrdaCodeId")
    DocumentConfigEntity partialUpdate(DocumentConfigReqDto documentConfigReqDto, @MappingTarget DocumentConfigEntity documentConfigEntity);

    /**
     * Converts a DocumentConfigEntity to a DocumentConfigResDto.
     *
     * @param documentConfigEntity the entity loaded from persistence
     * @return the response DTO suitable for serialization
     */
    DocumentConfigResDto toDto(DocumentConfigEntity documentConfigEntity);
}

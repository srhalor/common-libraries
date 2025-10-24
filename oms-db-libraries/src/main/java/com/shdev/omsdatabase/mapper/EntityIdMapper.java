package com.shdev.omsdatabase.mapper;

import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import com.shdev.omsdatabase.entity.ThBatchEntity;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * Helper mapper to convert simple Long IDs into lightweight entity references.
 * Useful when request DTOs carry only foreign key IDs.
 */
@Mapper(componentModel = SPRING)
public interface EntityIdMapper {

    /**
     * Create a ReferenceDataEntity stub with only the id populated.
     *
     * @param id the reference data id (maybe null)
     * @return a lightweight ReferenceDataEntity or null if id is null
     */
    default ReferenceDataEntity toReferenceData(Long id) {
        if (null == id) return null;
        return ReferenceDataEntity.builder()
                .id(id)
                .build();
    }

    /**
     * Create a DocumentRequestEntity stub with only the id populated.
     *
     * @param id the document request id (maybe null)
     * @return a lightweight DocumentRequestEntity or null if id is null
     */
    default DocumentRequestEntity toDocumentRequest(Long id) {
        if (null == id) return null;
        return DocumentRequestEntity.builder()
                .id(id)
                .build();
    }

    /**
     * Create a ThBatchEntity stub with only the id populated.
     *
     * @param id the Thunderhead batch id (maybe null)
     * @return a lightweight ThBatchEntity or null if id is null
     */
    default ThBatchEntity toThBatch(Long id) {
        if (null == id) return null;
        return ThBatchEntity.builder()
                .id(id)
                .build();
    }
}

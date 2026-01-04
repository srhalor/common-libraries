package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.RequestsMetadataValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for RequestsMetadataValueEntity, providing CRUD operations.
 *
 * @author Shailesh Halor
 */
public interface RequestsMetadataValueEntityRepository extends JpaRepository<RequestsMetadataValueEntity, Long> {

    /**
     * Find all metadata values for a specific document request.
     *
     * @param requestId the document request ID
     * @return list of metadata values
     */
    List<RequestsMetadataValueEntity> findByOmdrt_Id(Long requestId);
}

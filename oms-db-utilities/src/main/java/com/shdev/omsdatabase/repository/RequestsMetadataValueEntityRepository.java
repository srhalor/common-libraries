package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.RequestsMetadataValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for RequestsMetadataValueEntity, providing CRUD operations.
 *
 * @author Shailesh Halor
 */
public interface RequestsMetadataValueEntityRepository extends JpaRepository<RequestsMetadataValueEntity, Long> {
}

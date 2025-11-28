package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.DocumentRequestBlobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for DocumentRequestBlobEntity, providing CRUD operations.
 *
 * @author Shailesh Halor
 */
public interface DocumentRequestBlobEntityRepository extends JpaRepository<DocumentRequestBlobEntity, Long> {
}

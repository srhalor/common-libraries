package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for DocumentRequestEntity, providing CRUD operations.
 *
 * @author Shailesh Halor
 */
public interface DocumentRequestEntityRepository extends JpaRepository<DocumentRequestEntity, Long> {
}

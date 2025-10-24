package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for DocumentRequestEntity.
 *
 * @author Shailesh Halor
 */
public interface DocumentRequestRepository extends JpaRepository<DocumentRequestEntity, Long> {
}

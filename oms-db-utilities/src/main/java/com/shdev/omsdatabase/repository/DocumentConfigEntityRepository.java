package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.DocumentConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for DocumentConfigEntity, providing CRUD operations.
 *
 * @author Shailesh Halor
 */
public interface DocumentConfigEntityRepository extends JpaRepository<DocumentConfigEntity, Long> {
}

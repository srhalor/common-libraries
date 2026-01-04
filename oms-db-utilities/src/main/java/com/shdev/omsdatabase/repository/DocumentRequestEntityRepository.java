package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository interface for DocumentRequestEntity, providing CRUD operations and dynamic query support.
 *
 * @author Shailesh Halor
 */
public interface DocumentRequestEntityRepository extends JpaRepository<DocumentRequestEntity, Long>,
        JpaSpecificationExecutor<DocumentRequestEntity> {
}

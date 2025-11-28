package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.ThBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for ThBatchEntity, providing CRUD operations.
 *
 * @author Shailesh Halor
 */
public interface ThBatchEntityRepository extends JpaRepository<ThBatchEntity, Long> {
}

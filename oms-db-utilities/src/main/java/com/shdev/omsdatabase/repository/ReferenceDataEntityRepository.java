package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for ReferenceDataEntity, providing CRUD operations.
 *
 * @author Shailesh Halor
 */
public interface ReferenceDataEntityRepository extends JpaRepository<ReferenceDataEntity, Long> {
}

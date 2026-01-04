package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.ErrorDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for ErrorDetailEntity, providing CRUD operations.
 *
 * @author Shailesh Halor
 */
public interface ErrorDetailEntityRepository extends JpaRepository<ErrorDetailEntity, Long> {

    /**
     * Find all error details for a specific batch.
     *
     * @param batchId the batch ID
     * @return list of error details
     */
    List<ErrorDetailEntity> findByOmtbe_Id(Long batchId);
}

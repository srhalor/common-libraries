package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.ThBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for ThBatchEntity, providing CRUD operations.
 *
 * @author Shailesh Halor
 */
public interface ThBatchEntityRepository extends JpaRepository<ThBatchEntity, Long> {

    /**
     * Find all batches for a specific document request, ordered by creation date descending (newest first).
     *
     * @param requestId the document request ID
     * @return list of batches ordered by creation date descending
     */
    List<ThBatchEntity> findByOmdrt_IdOrderByCreatedDatDesc(Long requestId);
}

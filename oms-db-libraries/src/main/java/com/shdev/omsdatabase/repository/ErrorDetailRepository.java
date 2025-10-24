package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.ErrorDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for ErrorDetailEntity.
 *
 * @author Shailesh Halor
 */
public interface ErrorDetailRepository extends JpaRepository<ErrorDetailEntity, Long> {
}

package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.ErrorDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for ErrorDetailEntity, providing CRUD operations.
 *
 * @author Shailesh Halor
 */
public interface ErrorDetailEntityRepository extends JpaRepository<ErrorDetailEntity, Long> {
}

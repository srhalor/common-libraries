package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ReferenceDataEntity.
 * Provides basic CRUD and convenient lookups by type and name.
 *
 * @author Shailesh Halor
 */
public interface ReferenceDataRepository extends JpaRepository<ReferenceDataEntity, Long> {

    /**
     * Find a reference data entry by its type and name.
     *
     * @param refDataType the reference data type
     * @param refDataName the reference data name
     * @return an Optional containing the found ReferenceDataEntity, or empty if not found
     */
    Optional<ReferenceDataEntity> findByRefDataTypeAndRefDataName(String refDataType, String refDataName);

    /**
     * Check if a reference data entry exists by its type and name.
     *
     * @param refDataType the reference data type
     * @param refDataName the reference data name
     * @return true if an entry exists, false otherwise
     */
    boolean existsByRefDataTypeAndRefDataName(String refDataType, String refDataName);

    /**
     * Find all reference data entries of a given type.
     *
     * @param refDataType the reference data type
     * @return a list of ReferenceDataEntity matching the specified type
     */
    List<ReferenceDataEntity> findByRefDataType(String refDataType);
}

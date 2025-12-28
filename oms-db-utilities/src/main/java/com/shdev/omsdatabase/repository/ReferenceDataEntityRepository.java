package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository interface for ReferenceDataEntity, providing CRUD operations.
 *
 * @author Shailesh Halor
 */
public interface ReferenceDataEntityRepository extends JpaRepository<ReferenceDataEntity, Long> {

    /**
     * Find all reference data by type.
     *
     * @param refDataType the reference data type
     * @return list of reference data entities
     */
    List<ReferenceDataEntity> findByRefDataType(String refDataType);

    /**
     * Find all active reference data (where effect_to_dat >= current date).
     *
     * @param currentDate the current date for comparison
     * @return list of active reference data entities
     */
    @Query("SELECT r FROM ReferenceDataEntity r WHERE r.effectToDat >= :currentDate")
    List<ReferenceDataEntity> findAllActive(@Param("currentDate") OffsetDateTime currentDate);

    /**
     * Find active reference data by type (where effect_to_dat >= current date).
     *
     * @param refDataType the reference data type
     * @param currentDate the current date for comparison
     * @return list of active reference data entities matching the type
     */
    @Query("SELECT r FROM ReferenceDataEntity r WHERE r.refDataType = :refDataType AND r.effectToDat >= :currentDate")
    List<ReferenceDataEntity> findByRefDataTypeAndActive(@Param("refDataType") String refDataType,
                                                          @Param("currentDate") OffsetDateTime currentDate);

    /**
     * Find active reference data by type and value (where effect_to_dat >= current date).
     *
     * @param refDataType the reference data type
     * @param refDataValue the reference data value
     * @param currentDate the current date for comparison
     * @return list of active reference data entities matching the type and value
     */
    @Query("SELECT r FROM ReferenceDataEntity r WHERE r.refDataType = :refDataType AND r.refDataValue = :refDataValue AND r.effectToDat >= :currentDate")
    List<ReferenceDataEntity> findByRefDataTypeAndValueAndActive(@Param("refDataType") String refDataType,
                                                                  @Param("refDataValue") String refDataValue,
                                                                  @Param("currentDate") OffsetDateTime currentDate);
}



package com.shdev.omsdatabase.repository;

import com.shdev.omsdatabase.entity.DocumentConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository interface for DocumentConfigEntity, providing CRUD operations
 * and custom query methods for document configuration management.
 *
 * @author Shailesh Halor
 */
public interface DocumentConfigEntityRepository extends JpaRepository<DocumentConfigEntity, Long> {

    /**
     * Find all active document configurations (where effect_to_dat >= current date).
     *
     * @param currentDate the current date for comparison
     * @return list of active document configuration entities
     */
    @Query("SELECT d FROM DocumentConfigEntity d WHERE d.effectToDat >= :currentDate")
    List<DocumentConfigEntity> findAllActive(@Param("currentDate") OffsetDateTime currentDate);

    /**
     * Find active document configurations by business key and current timestamp.
     *
     * @param footerId      the footer ID
     * @param appDocSpecId  the app doc spec ID
     * @param codeId        the code ID
     * @param value         the configuration value
     * @param asOfDate      the date/time to check for active status
     * @return list of active document configuration entities
     */
    @Query("""
            SELECT d FROM DocumentConfigEntity d
            WHERE d.omrdaFooter.id = :footerId
            AND d.omrdaAppDocSpec.id = :appDocSpecId
            AND d.omrdaCode.id = :codeId
            AND d.value = :value
            AND d.effectFromDat <= :asOfDate
            AND d.effectToDat >= :asOfDate
            ORDER BY d.effectFromDat DESC
            """)
    List<DocumentConfigEntity> findByBusinessKeyAndActive(
            @Param("footerId") Long footerId,
            @Param("appDocSpecId") Long appDocSpecId,
            @Param("codeId") Long codeId,
            @Param("value") String value,
            @Param("asOfDate") OffsetDateTime asOfDate
    );

    /**
     * Search document configurations by footer value, document name, and code.
     * Returns configurations where footer.refDataValue matches footerValue,
     * appDocSpec.refDataValue starts with or equals documentName (handling wildcards),
     * and code.refDataValue equals codeValue.
     *
     * @param footerValue   the footer reference data value
     * @param documentName  the document name (app doc spec reference data value)
     * @param codeValue     the code reference data value
     * @return list of matching document configuration entities
     */
    @Query("""
            SELECT d FROM DocumentConfigEntity d
            WHERE d.omrdaFooter.refDataValue = :footerValue
            AND (d.omrdaAppDocSpec.refDataValue = '*'
                 OR d.omrdaAppDocSpec.refDataValue = :documentName
                 OR :documentName LIKE CONCAT(d.omrdaAppDocSpec.refDataValue, '%'))
            AND d.omrdaCode.refDataValue = :codeValue
            ORDER BY d.effectFromDat DESC
            """)
    List<DocumentConfigEntity> findByFooterAndDocumentNameAndCode(
            @Param("footerValue") String footerValue,
            @Param("documentName") String documentName,
            @Param("codeValue") String codeValue
    );

    /**
     * Search active document configurations by footer value, document name, and code.
     * Returns only active configurations (where effect_to_dat >= current date).
     *
     * @param footerValue   the footer reference data value
     * @param documentName  the document name (app doc spec reference data value)
     * @param codeValue     the code reference data value
     * @param currentDate   the current date for comparison
     * @return list of matching active document configuration entities
     */
    @Query("""
            SELECT d FROM DocumentConfigEntity d
            WHERE d.omrdaFooter.refDataValue = :footerValue
            AND (d.omrdaAppDocSpec.refDataValue = '*'
                 OR d.omrdaAppDocSpec.refDataValue = :documentName
                 OR :documentName LIKE CONCAT(d.omrdaAppDocSpec.refDataValue, '%'))
            AND d.omrdaCode.refDataValue = :codeValue
            AND d.effectToDat >= :currentDate
            ORDER BY d.effectFromDat DESC
            """)
    List<DocumentConfigEntity> findByFooterAndDocumentNameAndCodeActive(
            @Param("footerValue") String footerValue,
            @Param("documentName") String documentName,
            @Param("codeValue") String codeValue,
            @Param("currentDate") OffsetDateTime currentDate
    );
}




package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.constants.RefDataType;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import com.shdev.omsdatabase.exception.NotFoundException;
import com.shdev.omsdatabase.repository.ReferenceDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Type-safe service for querying reference data entries by type and name.
 * Intended as a reusable library API across services.
 *
 * @author Shailesh Halor
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReferenceDataService {

    private final ReferenceDataRepository repository;

    /**
     * Find a reference data row by type and name.
     *
     * @param type the reference data type
     * @param name the reference data name
     * @return an Optional containing the found ReferenceDataEntity, or empty if not found
     */
    public Optional<ReferenceDataEntity> findByTypeAndName(RefDataType type, String name) {
        log.debug("Finding reference data: type={}, name={}", type, name);
        return repository.findByRefDataTypeAndRefDataName(type.name(), name);
    }

    /**
     * Get a reference data row by type and name or throw if absent.
     *
     * @param type the reference data type
     * @param name the reference data name
     * @return the found ReferenceDataEntity
     * @throws NotFoundException if no matching reference data is found
     */
    public ReferenceDataEntity getByTypeAndNameOrThrow(RefDataType type, String name) {
        return findByTypeAndName(type, name)
                .orElseThrow(() -> new NotFoundException("Reference data not found: type=" + type + ", name=" + name));
    }

    /**
     * Return the id for a given type and name or throw if not found.
     *
     * @param type the reference data type
     * @param name the reference data name
     * @return the id of the found ReferenceDataEntity
     * @throws NotFoundException if no matching reference data is found
     */
    public Long getIdByTypeAndNameOrThrow(RefDataType type, String name) {
        return getByTypeAndNameOrThrow(type, name).getId();
    }

    /**
     * Check if a row exists for a given type and name.
     *
     * @param type the reference data type
     * @param name the reference data name
     * @return true if a matching reference data exists, false otherwise
     */
    public boolean exists(RefDataType type, String name) {
        return repository.existsByRefDataTypeAndRefDataName(type.name(), name);
    }

    /**
     * List all reference data rows for a given type.
     *
     * @param type the reference data type
     * @return list of ReferenceDataEntity matching the type
     */
    public List<ReferenceDataEntity> listByType(RefDataType type) {
        return repository.findByRefDataType(type.name());
    }

    /**
     * Convenience: find a METADATA_KEY by name.
     *
     * @param name the METADATA_KEY name
     * @return an Optional containing the found ReferenceDataEntity, or empty if not found
     */
    public Optional<ReferenceDataEntity> findMetadataKey(String name) {
        return findByTypeAndName(RefDataType.METADATA_KEY, name);
    }

    /**
     * Convenience: get a METADATA_KEY id by name or throw if absent.
     *
     * @param name the METADATA_KEY name
     * @return the id of the found METADATA_KEY
     * @throws NotFoundException if no matching METADATA_KEY is found
     */
    public Long getMetadataKeyIdOrThrow(String name) {
        return getByTypeAndNameOrThrow(RefDataType.METADATA_KEY, name).getId();
    }

    /**
     * Convenience: check existence of a METADATA_KEY by name.
     *
     * @param name the METADATA_KEY name
     * @return true if the METADATA_KEY exists, false otherwise
     */
    public boolean existsMetadataKey(String name) {
        return exists(RefDataType.METADATA_KEY, name);
    }

    /**
     * Convenience: list all METADATA_KEY rows.
     *
     * @return list of ReferenceDataEntity for METADATA_KEY type
     */
    public List<ReferenceDataEntity> listMetadataKeys() {
        return listByType(RefDataType.METADATA_KEY);
    }
}

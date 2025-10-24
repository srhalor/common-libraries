package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.exception.BadRequestException;
import com.shdev.omsdatabase.repository.DocumentRequestRepository;
import com.shdev.omsdatabase.repository.ReferenceDataRepository;
import com.shdev.omsdatabase.repository.ThBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Small helper component to validate required FK references exist before persisting entities.
 *
 * @author Shailesh Halor
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ForeignKeyValidator {

    private final ReferenceDataRepository referenceDataRepository;
    private final DocumentRequestRepository documentRequestRepository;
    private final ThBatchRepository thBatchRepository;

    /**
     * Ensure a reference data id exists.
     *
     * @param id    the reference data id
     * @param field the name of the field being validated
     */
    public void requireReferenceData(Long id, String field) {
        if (null == id || !referenceDataRepository.existsById(id)) {
            log.warn("Invalid reference id for {}: {}", field, id);
            throw new BadRequestException("Invalid reference id for " + field + ": " + id);
        }
    }

    /**
     * Ensure a document request id exists..
     *
     * @param id    the document request id
     * @param field the name of the field being validated
     */
    public void requireDocumentRequest(Long id, String field) {
        if (null == id || !documentRequestRepository.existsById(id)) {
            log.warn("Invalid document request id for {}: {}", field, id);
            throw new BadRequestException("Invalid document request id for " + field + ": " + id);
        }
    }

    /**
     * Ensure a Thunderhead batch id exists.
     *
     * @param id    the TH batch id
     * @param field the name of the field being validated
     */
    public void requireThBatch(Long id, String field) {
        if (null == id || !thBatchRepository.existsById(id)) {
            log.warn("Invalid TH batch id for {}: {}", field, id);
            throw new BadRequestException("Invalid TH batch id for " + field + ": " + id);
        }
    }
}

package com.shdev.omsdatabase.testsupport;

import com.shdev.omsdatabase.constants.RefDataType;
import com.shdev.omsdatabase.dto.request.*;
import com.shdev.omsdatabase.entity.*;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Factory class for creating test data entities and DTOs.
 */
public final class TestDataFactory {
    private TestDataFactory() {
    }

    /**
     * Reference Data Entity builder
     *
     * @param id   ID of the ReferenceDataEntity
     * @param type RefDataType enum for the reference data type
     * @param name Name of the reference data
     * @return Constructed ReferenceDataEntity
     */
    public static ReferenceDataEntity refData(Long id, RefDataType type, String name) {
        ReferenceDataEntity e = new ReferenceDataEntity();
        e.setId(id);
        e.setRefDataType(type.name());
        e.setRefDataName(name);
        e.setEffectFromDat(LocalDate.now().minusDays(1));
        e.setEffectToDat(LocalDate.now().plusDays(365));
        e.setCreatedDat(Instant.now());
        e.setLastUpdateDat(Instant.now());
        e.setCreateUid("test");
        e.setLastUpdateUid("test");
        return e;
    }

    /**
     * Document Request Entity builder
     *
     * @param id     ID of the DocumentRequestEntity
     * @param source ReferenceDataEntity for source system
     * @param type   ReferenceDataEntity for document type
     * @param name   ReferenceDataEntity for document name
     * @param status ReferenceDataEntity for document status
     * @return Constructed DocumentRequestEntity
     */
    public static DocumentRequestEntity docReq(Long id,
                                               ReferenceDataEntity source,
                                               ReferenceDataEntity type,
                                               ReferenceDataEntity name,
                                               ReferenceDataEntity status) {
        DocumentRequestEntity e = new DocumentRequestEntity();
        e.setId(id);
        e.setOmrdaSourceSystem(source);
        e.setOmrdaDocumentType(type);
        e.setOmrdaDocumentName(name);
        e.setOmrdaDocStatus(status);
        e.setCreatedDat(Instant.now());
        e.setLastUpdateDat(Instant.now());
        e.setCreateUidHeader("hdr");
        e.setCreateUidToken("tok");
        return e;
    }

    /**
     * Document Config Entity builder
     *
     * @param id         ID of the DocumentConfigEntity
     * @param footer     ReferenceDataEntity for footer
     * @param appDocSpec ReferenceDataEntity for application document specification
     * @param code       ReferenceDataEntity for code
     * @param value      Value string for the document configuration
     * @return Constructed DocumentConfigEntity
     */
    public static DocumentConfigEntity docConfig(Long id,
                                                 ReferenceDataEntity footer,
                                                 ReferenceDataEntity appDocSpec,
                                                 ReferenceDataEntity code,
                                                 String value) {
        DocumentConfigEntity e = new DocumentConfigEntity();
        e.setId(id);
        e.setOmrdaFooter(footer);
        e.setOmrdaAppDocSpec(appDocSpec);
        e.setOmrdaCode(code);
        e.setValue(value);
        e.setEffectFromDat(LocalDate.now().minusDays(1));
        e.setEffectToDat(LocalDate.now().plusDays(30));
        e.setCreatedDat(Instant.now());
        e.setLastUpdateDat(Instant.now());
        e.setCreateUid("test");
        e.setLastUpdateUid("test");
        return e;
    }

    /**
     * TH Batch Entity builder
     *
     * @param id     ID of the ThBatchEntity
     * @param req    DocumentRequestEntity associated with the TH batch
     * @param status ReferenceDataEntity for TH batch status
     * @return Constructed ThBatchEntity
     */
    public static ThBatchEntity thBatch(Long id, DocumentRequestEntity req, ReferenceDataEntity status) {
        ThBatchEntity e = new ThBatchEntity();
        e.setId(id);
        e.setOmdrt(req);
        e.setOmrdaThStatus(status);
        e.setThBatchId(42L);
        e.setBatchName("B");
        e.setSyncStatus(Boolean.FALSE);
        e.setEventStatus(Boolean.FALSE);
        e.setRetryCount(0L);
        e.setCreatedDat(Instant.now());
        e.setLastUpdateDat(Instant.now());
        e.setCreateUidHeader("hdr");
        e.setCreateUidToken("tok");
        return e;
    }

    /**
     * Error Detail Entity builder
     *
     * @param id  ID of the ErrorDetailEntity
     * @param thb ThBatchEntity associated with the error detail
     * @return Constructed ErrorDetailEntity
     */
    public static ErrorDetailEntity errorDetail(Long id, ThBatchEntity thb) {
        ErrorDetailEntity e = new ErrorDetailEntity();
        e.setId(id);
        e.setOmtbe(thb);
        e.setErrorCategory("VALIDATION_ERROR");
        e.setErrorDescription("desc");
        return e;
    }

    /**
     * Requests Metadata Value Entity builder
     *
     * @param id    ID of the RequestsMetadataValueEntity
     * @param req   DocumentRequestEntity associated with the metadata value
     * @param key   ReferenceDataEntity for the metadata key
     * @param value Value string for the metadata
     * @return Constructed RequestsMetadataValueEntity
     */
    public static RequestsMetadataValueEntity reqMetaVal(Long id, DocumentRequestEntity req, ReferenceDataEntity key, String value) {
        RequestsMetadataValueEntity e = new RequestsMetadataValueEntity();
        e.setId(id);
        e.setOmdrt(req);
        e.setOmrda(key);
        e.setMetadataValue(value);
        return e;
    }

    // DTO builders

    /**
     * Document Request Request DTO builder
     *
     * @param src    Source system ID
     * @param type   Document type ID
     * @param name   Document name ID
     * @param status Document status ID
     * @return Constructed DocumentRequestReqDto
     */
    public static DocumentRequestReqDto docReqDto(long src, long type, long name, long status) {
        return new DocumentRequestReqDto(src, type, name, status);
    }

    /**
     * Document Config Request DTO builder
     *
     * @param footer     Footer ID
     * @param appDocSpec Application document specification ID
     * @param code       Code ID
     * @param value      Value string
     * @return Constructed DocumentConfigReqDto
     */
    public static DocumentConfigReqDto docConfigDto(long footer, long appDocSpec, long code, String value) {
        return new DocumentConfigReqDto(footer, appDocSpec, code, value, null, LocalDate.now(), LocalDate.now().plusDays(1));
    }

    /**
     * TH Batch Request DTO builder
     *
     * @param reqId     Document request ID
     * @param thBatchId TH batch ID
     * @param statusId  TH batch status ID
     * @return Constructed ThBatchReqDto
     */
    public static ThBatchReqDto thBatchDto(long reqId, long thBatchId, long statusId) {
        return new ThBatchReqDto(reqId, thBatchId, statusId, "BATCH", null, false, false, 0L);
    }

    /**
     * Error Detail Request DTO builder
     *
     * @param batchId TH batch ID
     * @return Constructed ErrorDetailReqDto
     */
    public static ErrorDetailReqDto errorDetailDto(long batchId) {
        return new ErrorDetailReqDto(batchId, "VALIDATION_ERROR", "desc");
    }

    /**
     * Requests Metadata Value Request DTO builder
     *
     * @param reqId Document request ID
     * @param keyId Metadata key ID
     * @param value Metadata value string
     * @return Constructed RequestsMetadataValueReqDto
     */
    public static RequestsMetadataValueReqDto metaValDto(long reqId, long keyId, String value) {
        return new RequestsMetadataValueReqDto(reqId, keyId, value);
    }

    /**
     * Document Request Blob Request DTO builder
     *
     * @param reqId Document request ID
     * @param json  JSON string
     * @param xml   XML string
     * @return Constructed DocumentRequestBlobReqDto
     */
    public static DocumentRequestBlobReqDto blobDto(long reqId, String json, String xml) {
        return new DocumentRequestBlobReqDto(reqId, json, xml);
    }
}


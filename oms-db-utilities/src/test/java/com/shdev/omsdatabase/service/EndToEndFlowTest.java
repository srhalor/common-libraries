package com.shdev.omsdatabase.service;

import com.shdev.omsdatabase.dto.request.*;
import com.shdev.omsdatabase.dto.response.*;
import com.shdev.omsdatabase.entity.*;
import com.shdev.omsdatabase.mapper.*;
import com.shdev.omsdatabase.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * End-to-end flow test covering creation of DocumentRequest, ThBatch, DocumentRequestBlob,
 * RequestsMetadataValue, and ErrorDetail entities and their interactions.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("End-to-End Flow Test")
class EndToEndFlowTest {

    @InjectMocks private DocumentRequestService documentRequestService;
    @InjectMocks private ThBatchService thBatchService;
    @InjectMocks private ErrorDetailService errorDetailService;
    @InjectMocks private DocumentRequestBlobService blobService;
    @InjectMocks private RequestsMetadataValueService metadataValueService;
    @Mock private ForeignKeyValidator fk;

    @Mock private DocumentRequestRepository documentRequestRepository;
    @Mock private ReferenceDataRepository referenceDataRepository;
    @Mock private ThBatchRepository thBatchRepository;
    @Mock private ErrorDetailRepository errorDetailRepository;
    @Mock private DocumentRequestBlobRepository documentRequestBlobRepository;
    @Mock private RequestsMetadataValueRepository requestsMetadataValueRepository;

    @Mock private DocumentRequestMapper documentRequestMapper;
    @Mock private ThBatchMapper thBatchMapper;
    @Mock private DocumentRequestBlobMapper documentRequestBlobMapper;
    @Mock private RequestsMetadataValueMapper requestsMetadataValueMapper;
    @Mock private ErrorDetailMapper errorDetailMapper;

    /**
     * Test: Full flow - create DocumentRequest, ThBatch, DocumentRequestBlob,
     * RequestsMetadataValue, and ErrorDetail
     * Given: Valid request DTOs for each entity
     * When: Each create method is called in sequence
     * Then: Each entity is created successfully and essential interactions are verified
     */
    @Test
    @DisplayName("Full Flow: create DocumentRequest -> ThBatch -> Blob -> Metadata -> ErrorDetail")
    void full_flow_createRequest_thenBatch_thenBlob_thenMetadata_thenError() {
        long src=1, typ=2, nam=3, st=4, batchSt=5, metaKey=6;

        // DocumentRequest
        when(documentRequestMapper.toEntity(any(DocumentRequestReqDto.class))).thenReturn(new DocumentRequestEntity());
        when(documentRequestRepository.save(any(DocumentRequestEntity.class)))
                .thenAnswer(inv -> { DocumentRequestEntity e = inv.getArgument(0); e.setId(1000L); return e; });
        when(documentRequestMapper.toDto(any(DocumentRequestEntity.class)))
                .thenReturn(new DocumentRequestResDto(1000L, null, null, null, null, null, null, null, null));
        var docReqRes = documentRequestService.create(new DocumentRequestReqDto(src, typ, nam, st));
        assertThat(docReqRes.id()).isEqualTo(1000L);

        // ThBatch
        when(thBatchMapper.toEntity(any(ThBatchReqDto.class))).thenReturn(new ThBatchEntity());
        when(thBatchRepository.save(any(ThBatchEntity.class)))
                .thenAnswer(inv -> { ThBatchEntity e = inv.getArgument(0); e.setId(2000L); return e; });
        when(thBatchMapper.toDto(any(ThBatchEntity.class)))
                .thenReturn(new ThBatchResDto(2000L, 1000L, 99L, null, "B1", null, false, false, 0L, null, null, null, null));
        var batchRes = thBatchService.create(new ThBatchReqDto(1000L, 99L, batchSt, "B1", null, false, false, 0L));
        assertThat(batchRes.id()).isEqualTo(2000L);

        // Blob
        when(documentRequestBlobMapper.toEntity(any(DocumentRequestBlobReqDto.class))).thenReturn(new DocumentRequestBlobEntity());
        when(documentRequestBlobRepository.save(any(DocumentRequestBlobEntity.class)))
                .thenAnswer(inv -> { DocumentRequestBlobEntity e = inv.getArgument(0); e.setId(1000L); return e; });
        when(documentRequestBlobMapper.toDto(any(DocumentRequestBlobEntity.class)))
                .thenReturn(new DocumentRequestBlobResDto(1000L, null, "{json}", "<xml/>"));
        var blobRes = blobService.create(new DocumentRequestBlobReqDto(1000L, "{json}", "<xml/>"));
        assertThat(blobRes.id()).isEqualTo(1000L);

        // Metadata
        when(requestsMetadataValueMapper.toEntity(any(RequestsMetadataValueReqDto.class))).thenReturn(new RequestsMetadataValueEntity());
        when(requestsMetadataValueRepository.save(any(RequestsMetadataValueEntity.class)))
                .thenAnswer(inv -> { RequestsMetadataValueEntity e = inv.getArgument(0); e.setId(3000L); return e; });
        when(requestsMetadataValueMapper.toDto(any(RequestsMetadataValueEntity.class)))
                .thenReturn(new RequestsMetadataValueResDto(3000L, null, null, "VAL"));
        var metaRes = metadataValueService.create(new RequestsMetadataValueReqDto(1000L, metaKey, "VAL"));
        assertThat(metaRes.id()).isEqualTo(3000L);

        // ErrorDetail
        when(errorDetailMapper.toEntity(any(ErrorDetailReqDto.class))).thenReturn(new ErrorDetailEntity());
        when(errorDetailRepository.save(any(ErrorDetailEntity.class)))
                .thenAnswer(inv -> { ErrorDetailEntity e = inv.getArgument(0); e.setId(4000L); return e; });
        when(errorDetailMapper.toDto(any(ErrorDetailEntity.class)))
                .thenReturn(new ErrorDetailResDto(4000L, null, "VALIDATION_ERROR", "bad input"));
        var errRes = errorDetailService.create(new ErrorDetailReqDto(2000L, "VALIDATION_ERROR", "bad input"));
        assertThat(errRes.id()).isEqualTo(4000L);

        // verify essential interactions
        verify(documentRequestRepository).save(any());
        verify(thBatchRepository).save(any());
        verify(documentRequestBlobRepository).save(any());
        verify(requestsMetadataValueRepository).save(any());
        verify(errorDetailRepository).save(any());
    }
}

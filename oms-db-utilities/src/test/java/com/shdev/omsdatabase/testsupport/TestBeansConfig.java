package com.shdev.omsdatabase.testsupport;

import com.shdev.omsdatabase.mapper.*;
import com.shdev.omsdatabase.repository.*;
import com.shdev.omsdatabase.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

/**
 * Test configuration defining beans for services and default repository mocks.
 */
@Configuration
@ComponentScan(basePackages = {
        "com.shdev.omsdatabase.mapper"
})
public class TestBeansConfig {

    // Default repository mocks, can be overridden by @MockBean in individual tests
    @Bean public DocumentRequestRepository documentRequestRepository() { return mock(DocumentRequestRepository.class); }
    @Bean public DocumentRequestBlobRepository documentRequestBlobRepository() { return mock(DocumentRequestBlobRepository.class); }
    @Bean public DocumentConfigRepository documentConfigRepository() { return mock(DocumentConfigRepository.class); }
    @Bean public ErrorDetailRepository errorDetailRepository() { return mock(ErrorDetailRepository.class); }
    @Bean public ReferenceDataRepository referenceDataRepository() { return mock(ReferenceDataRepository.class); }
    @Bean public RequestsMetadataValueRepository requestsMetadataValueRepository() { return mock(RequestsMetadataValueRepository.class); }
    @Bean public ThBatchRepository thBatchRepository() { return mock(ThBatchRepository.class); }

    @Bean
    public ForeignKeyValidator foreignKeyValidator(ReferenceDataRepository referenceDataRepository,
                                                   DocumentRequestRepository documentRequestRepository,
                                                   ThBatchRepository thBatchRepository) {
        return new ForeignKeyValidator(referenceDataRepository, documentRequestRepository, thBatchRepository);
    }

    @Bean
    public ReferenceDataService referenceDataService(ReferenceDataRepository repository) {
        return new ReferenceDataService(repository);
    }

    @Bean
    public DocumentRequestService documentRequestService(DocumentRequestRepository repository,
                                                         DocumentRequestMapper mapper,
                                                         ForeignKeyValidator fk) {
        return new DocumentRequestService(repository, mapper, fk);
    }

    @Bean
    public DocumentConfigService documentConfigService(DocumentConfigRepository repository,
                                                       DocumentConfigMapper mapper,
                                                       ForeignKeyValidator fk) {
        return new DocumentConfigService(repository, mapper, fk);
    }

    @Bean
    public DocumentRequestBlobService documentRequestBlobService(DocumentRequestBlobRepository repository,
                                                                 DocumentRequestBlobMapper mapper,
                                                                 ForeignKeyValidator fk) {
        return new DocumentRequestBlobService(repository, mapper, fk);
    }

    @Bean
    public RequestsMetadataValueService requestsMetadataValueService(RequestsMetadataValueRepository repository,
                                                                     RequestsMetadataValueMapper mapper,
                                                                     ForeignKeyValidator fk) {
        return new RequestsMetadataValueService(repository, mapper, fk);
    }

    @Bean
    public ErrorDetailService errorDetailService(ErrorDetailRepository repository,
                                                 ErrorDetailMapper mapper,
                                                 ForeignKeyValidator fk) {
        return new ErrorDetailService(repository, mapper, fk);
    }

    @Bean
    public ThBatchService thBatchService(ThBatchRepository repository,
                                         ThBatchMapper mapper,
                                         ForeignKeyValidator fk) {
        return new ThBatchService(repository, mapper, fk);
    }
}

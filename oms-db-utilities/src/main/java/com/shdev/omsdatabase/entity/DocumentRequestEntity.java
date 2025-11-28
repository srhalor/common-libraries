package com.shdev.omsdatabase.entity;

import com.shdev.omsdatabase.entity.base.DualCreateUidEntity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

/**
 * Entity representing document requests and their overall processing status.
 *
 * @author Shailesh Halor
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TBOM_DOCUMENT_REQUESTS")
@DynamicInsert
@DynamicUpdate
@Comment("Table to store document requests and their overall processing status.")
public class DocumentRequestEntity extends DualCreateUidEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBOM_DOCUMENT_REQUESTS_id_gen")
    @SequenceGenerator(name = "TBOM_DOCUMENT_REQUESTS_id_gen", sequenceName = "SQOMRDA_DOC_REQUEST_ID", allocationSize = 1)
    @Comment("Primary metadataKey for document requests.")
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Foreign metadataKey to tbom_reference_data (SOURCE_SYSTEM) identifying the Source System making the request.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_SOURCE_SYSTEM_ID", nullable = false)
    private ReferenceDataEntity omrdaSourceSystem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Foreign metadataKey to tbom_reference_data (DOCUMENT_TYPE) identifying the refDataType of document requested.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_DOCUMENT_TYPE_ID", nullable = false)
    private ReferenceDataEntity omrdaDocumentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Foreign metadataKey to tbom_reference_data (DOCUMENT_NAME) identifying the refDataValue of the document requested.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_DOCUMENT_NAME_ID", nullable = false)
    private ReferenceDataEntity omrdaDocumentName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Foreign metadataKey to tbom_reference_data (DOCUMENT_STATUS) indicating request processing status.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_DOC_STATUS_ID", nullable = false)
    private ReferenceDataEntity omrdaDocStatus;

}

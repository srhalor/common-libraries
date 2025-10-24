package com.shdev.omsdatabase.entity;

import com.shdev.omsdatabase.util.AuditEntityListener;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.*;

import java.io.Serializable;
import java.time.Instant;

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
@EntityListeners(AuditEntityListener.class)
@DynamicInsert
@DynamicUpdate
@Comment("Table to store document requests and their overall processing status.")
public class DocumentRequestEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBOM_DOCUMENT_REQUESTS_id_gen")
    @SequenceGenerator(name = "TBOM_DOCUMENT_REQUESTS_id_gen", sequenceName = "SQOMRDA_DOC_REQUEST_ID", allocationSize = 1)
    @Comment("Primary key for document requests.")
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Foreign key to tbom_reference_data (SOURCE_SYSTEM) identifying the Source System making the request.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_SOURCE_SYSTEM_ID", nullable = false)
    private ReferenceDataEntity omrdaSourceSystem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Foreign key to tbom_reference_data (DOCUMENT_TYPE) identifying the type of document requested.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_DOCUMENT_TYPE_ID", nullable = false)
    private ReferenceDataEntity omrdaDocumentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Foreign key to tbom_reference_data (DOCUMENT_NAME) identifying the name of the document requested.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_DOCUMENT_NAME_ID", nullable = false)
    private ReferenceDataEntity omrdaDocumentName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Foreign key to tbom_reference_data (DOCUMENT_STATUS) indicating request processing status.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_DOC_STATUS_ID", nullable = false)
    private ReferenceDataEntity omrdaDocStatus;

    @Comment("Record creation timestamp. Set by DB trigger when omitted.")
    @Column(name = "CREATED_DAT", nullable = false)
    private Instant createdDat;

    @Comment("Record last update timestamp. Set by DB trigger when omitted.")
    @Column(name = "LAST_UPDATE_DAT", nullable = false)
    private Instant lastUpdateDat;

    @Size(max = 20)
    @Comment("User ID from request header when creating the record. If not provided, DB trigger should set it.")
    @Column(name = "CREATE_UID_HEADER", nullable = false, length = 20)
    private String createUidHeader;

    @Size(max = 20)
    @Comment("User ID from JWT token when creating the record. If not provided, DB trigger should set it.")
    @Column(name = "CREATE_UID_TOKEN", nullable = false, length = 20)
    private String createUidToken;

}

package com.shdev.omsdatabase.entity;

import com.shdev.omsdatabase.entity.base.DualCreateUidEntity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.*;

import java.io.Serializable;

/**
 * Entity representing Thunderhead batch details associated with document requests.
 *
 * @author Shailesh Halor
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TBOM_TH_BATCHES")
@DynamicInsert
@DynamicUpdate
@Comment("Thunderhead batch details associated with document requests.")
public class ThBatchEntity extends DualCreateUidEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBOM_TH_BATCHES_id_gen")
    @SequenceGenerator(name = "TBOM_TH_BATCHES_id_gen", sequenceName = "SQOMTHB_TH_BATCH_ID", allocationSize = 1)
    @Comment("Primary key for Thunderhead batch.")
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Foreign key to tbom_document_requests(id). One request can have multiple batches.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMDRT_ID", nullable = false)
    private DocumentRequestEntity omdrt;

    @Comment("External Thunderhead batch identifier; defaults to 10000 if not provided.")
    @ColumnDefault("10000")
    @Column(name = "TH_BATCH_ID", nullable = false)
    private Long thBatchId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Foreign key to tbom_reference_data(id) with type BATCH_STATUS indicating batch processing status.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_TH_STATUS_ID", nullable = false)
    private ReferenceDataEntity omrdaThStatus;

    @Size(max = 100)
    @Comment("Batch name passed to Thunderhead when creating the batch.")
    @Column(name = "BATCH_NAME", nullable = false, length = 100)
    private String batchName;

    @Comment("Identifier of the generated document in DMS (nullable until available).")
    @Column(name = "DMS_DOCUMENT_ID")
    private Long dmsDocumentId;

    @Comment("Flag indicating whether the Thunderhead batch status has been synchronized back to OMS.")
    @ColumnDefault("'N'")
    @Column(name = "SYNC_STATUS", nullable = false, length = 1)
    private Boolean syncStatus;

    @Comment("Flag indicating whether the batch status event has been published.")
    @ColumnDefault("'N'")
    @Column(name = "EVENT_STATUS", nullable = false, length = 1)
    private Boolean eventStatus;

    @Comment("Number of retry attempts for synchronization and event publishing.")
    @ColumnDefault("0")
    @Column(name = "RETRY_COUNT", nullable = false)
    private Long retryCount;

}

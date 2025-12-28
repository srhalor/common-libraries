package com.shdev.omsdatabase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

/**
 * Entity storing actual metadata values for each tbom_document_requests row.
 *
 * @author Shailesh Halor
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TBOM_REQUESTS_METADATA_VALUES")
@Comment("Stores actual metadata values for each tbom_document_requests row.")
public class RequestsMetadataValueEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBOM_REQUESTS_METADATA_VALUES_id_gen")
    @SequenceGenerator(name = "TBOM_REQUESTS_METADATA_VALUES_id_gen", sequenceName = "SQOMRME_METADATA_VALUE_ID", allocationSize = 1)
    @Comment("Primary metadataKey for metadata metadataValue.")
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMDRT_ID", nullable = false)
    private DocumentRequestEntity omdrt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_ID", nullable = false)
    private ReferenceDataEntity omrda;

    @Size(max = 255)
    @Comment("Concrete metadataValue for the referenced metadata metadataKey (string up to 255 chars).")
    @Column(name = "METADATA_VALUE", nullable = false)
    private String metadataValue;

}

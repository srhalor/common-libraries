package com.shdev.omsdatabase.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

/**
 * Entity storing JSON and optional XML request payloads for a single tbom_document_requests row (1:1 mapping via PK=FK).
 *
 * @author Shailesh Halor
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TBOM_DOCUMENT_REQUESTS_BLOB")
@Comment("Stores JSON and optional XML request payloads for a single tbom_document_requests row (1:1 mapping via PK=FK).")
public class DocumentRequestBlobEntity implements Serializable {

    @Id
    @Comment("Identifier matching tbom_document_requests.id (1:1); serves as both primary metadataKey and foreign metadataKey.")
    @Column(name = "OMDRT_ID", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "OMDRT_ID", nullable = false)
    private DocumentRequestEntity tbomDocumentRequests;

    @Comment("Request payload in JSON format; required.")
    @Lob
    @Column(name = "JSON_REQUEST", nullable = false)
    private String jsonRequest;

    @Comment("Request payload in XML format; optional.")
    @Lob
    @Column(name = "XML_REQUEST")
    private String xmlRequest;

}

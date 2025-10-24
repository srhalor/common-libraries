package com.shdev.omsdatabase.entity;

import com.shdev.omsdatabase.util.AuditEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Entity representing reference data used in the OMS system.
 *
 * @author Shailesh Halor
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TBOM_REFERENCE_DATA")
@EntityListeners(AuditEntityListener.class)
@DynamicInsert
@DynamicUpdate
@Comment("Central table to store reference data values used by oms system (document types, document names, metadata keys, source systems, etc.).")
public class ReferenceDataEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBOM_REFERENCE_DATA_id_gen")
    @SequenceGenerator(name = "TBOM_REFERENCE_DATA_id_gen", sequenceName = "SQOMRDA_REF_DATA_ID", allocationSize = 1)
    @Comment("Primary Key for reference data.")
    @Column(name = "ID", nullable = false)
    private Long id;

    @Size(max = 50)
    @Comment("Type of reference data (e.g. DOCUMENT_TYPE, DOCUMENT_NAME, METADATA_KEY, SOURCE_SYSTEM).")
    @Column(name = "REF_DATA_TYPE", nullable = false, length = 50)
    private String refDataType;

    @Size(max = 100)
    @Comment("Name or value of the reference data (e.g. Invoice, IVZRECPA, 103, IV, MetadataKey1).")
    @Column(name = "REF_DATA_NAME", nullable = false, length = 100)
    private String refDataName;

    @Size(max = 255)
    @Comment("Optional description for the reference data value.")
    @Column(name = "DESCRIPTION")
    private String description;

    @Comment("Date from which this reference is effective.")
    @Column(name = "EFFECT_FROM_DAT", nullable = false)
    private LocalDate effectFromDat;

    @Comment("Date till which this reference is effective.")
    @Column(name = "EFFECT_TO_DAT", nullable = false)
    private LocalDate effectToDat;

    @Comment("Record creation timestamp.")
    @Column(name = "CREATED_DAT", nullable = false)
    private Instant createdDat;

    @Comment("Record last update timestamp.")
    @Column(name = "LAST_UPDATE_DAT", nullable = false)
    private Instant lastUpdateDat;

    @Size(max = 20)
    @Comment("User ID who created the record. If not provided, DB trigger should set it.")
    @Column(name = "CREATE_UID", nullable = false, length = 20)
    private String createUid;

    @Size(max = 20)
    @Comment("User ID who last updated the record. If not provided, DB trigger should set it.")
    @Column(name = "LAST_UPDATE_UID", nullable = false, length = 20)
    private String lastUpdateUid;

}

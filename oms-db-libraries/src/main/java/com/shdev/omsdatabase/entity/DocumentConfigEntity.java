package com.shdev.omsdatabase.entity;

import com.shdev.omsdatabase.util.AuditEntityListener;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Entity representing effective-dated document configuration key/value mappings.
 * References tbom_reference_data for footer, application document specification, and configuration code.
 *
 * @author Shailesh Halor
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TBOM_DOCUMENT_CONFIGURATIONS")
@EntityListeners(AuditEntityListener.class)
@DynamicInsert
@DynamicUpdate
@Comment("Effective-dated document configuration key/value mappings. References tbom_reference_data for footer, application document specification, and configuration code.")
public class DocumentConfigEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBOM_DOCUMENT_CONFIGURATIONS_id_gen")
    @SequenceGenerator(name = "TBOM_DOCUMENT_CONFIGURATIONS_id_gen", sequenceName = "SQOMDCN_DOC_CONFIG_ID", allocationSize = 1)
    @Comment("Primary key for document configuration row.")
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("FK to tbom_reference_data (FOOTER_ID) identifying the footer.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_FOOTER_ID", nullable = false)
    private ReferenceDataEntity omrdaFooter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("FK to tbom_reference_data (APP_DOC_SPEC) identifying the application document specification.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_APP_DOC_SPEC_ID", nullable = false)
    private ReferenceDataEntity omrdaAppDocSpec;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("FK to tbom_reference_data (DOC_CONFIG_CODE) identifying the configuration code.")
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMRDA_CODE_ID", nullable = false)
    private ReferenceDataEntity omrdaCode;

    @Size(max = 255)
    @Comment("Configuration value associated with the DOC_CONFIG_CODE.")
    @Column(name = "VALUE", nullable = false)
    private String value;

    @Size(max = 255)
    @Comment("Optional description of the document configuration value.")
    @Column(name = "DESCRIPTION")
    private String description;

    @Comment("Date/time from which this configuration row becomes effective.")
    @Column(name = "EFFECT_FROM_DAT", nullable = false)
    private LocalDate effectFromDat;

    @Comment("Date/time until which this configuration row remains effective.")
    @Column(name = "EFFECT_TO_DAT", nullable = false)
    private LocalDate effectToDat;

    @Comment("Record creation timestamp. Set by DB trigger when omitted.")
    @Column(name = "CREATED_DAT", nullable = false)
    private Instant createdDat;

    @Comment("Record last update timestamp. Set by DB trigger when omitted.")
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

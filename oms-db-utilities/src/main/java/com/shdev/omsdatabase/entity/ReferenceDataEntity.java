package com.shdev.omsdatabase.entity;

import com.shdev.omsdatabase.entity.base.SingleAuditUidEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
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
@DynamicInsert
@DynamicUpdate
@Comment("Central table to store reference data values used by oms system (document types, document names, metadata keys, source systems, etc.).")
public class ReferenceDataEntity extends SingleAuditUidEntity implements Serializable {

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
    @Comment("Name or metadataValue of the reference data (e.g. Invoice, IVZRECPA, 103, IV, MetadataKey1).")
    @Column(name = "REF_DATA_VALUE", nullable = false, length = 100)
    private String refDataValue;

    @Comment("Indicates if the reference data metadataValue is editable ('Y'/'N').")
    @ColumnDefault("'N'")
    @Column(name = "EDITABLE", nullable = false, length = 1)
    private Boolean editable;

    @Size(max = 255)
    @Comment("Optional description for the reference data metadataValue.")
    @Column(name = "DESCRIPTION")
    private String description;

    @Comment("Date from which this reference is effective.")
    @Column(name = "EFFECT_FROM_DAT", nullable = false)
    private LocalDate effectFromDat;

    @Comment("Date till which this reference is effective.")
    @Column(name = "EFFECT_TO_DAT", nullable = false)
    private LocalDate effectToDat;

}

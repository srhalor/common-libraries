package com.shdev.omsdatabase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

/**
 * Entity capturing error details encountered during processing of a Document request or Thunderhead batch.
 *
 * @author Shailesh Halor
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TBOM_ERROR_DETAILS")
@Comment("Captures error details encountered during processing of a Document request or Thunderhead batch.")
public class ErrorDetailEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TBOM_ERROR_DETAILS_id_gen")
    @SequenceGenerator(name = "TBOM_ERROR_DETAILS_id_gen", sequenceName = "SQOMEDL_ERROR_DETAILS_ID", allocationSize = 1)
    @Comment("Primary metadataKey for error details.")
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "OMTBE_ID", nullable = false)
    private ThBatchEntity omtbe;

    @Size(max = 100)
    @Comment("Error category or refDataType (e.g., VALIDATION_ERROR, TIMEOUT, INTERNAL_ERROR).")
    @Column(name = "ERROR_CATEGORY", nullable = false, length = 100)
    private String errorCategory;

    @Comment("Detailed error description or message.")
    @Lob
    @Column(name = "ERROR_DESCRIPTION", nullable = false)
    private String errorDescription;

}

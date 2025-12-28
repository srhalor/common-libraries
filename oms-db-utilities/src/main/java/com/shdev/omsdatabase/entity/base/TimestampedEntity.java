package com.shdev.omsdatabase.entity.base;

import com.shdev.omsdatabase.util.AuditEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.OffsetDateTime;

/**
 * Base mapped superclass providing common timestamp audit fields present on OMS entities.
 * These timestamps are managed by database triggers (insertable=false, updatable=false) and
 * therefore not set in application code.
 *
 * @author Shailesh Halor
 */
@MappedSuperclass
@EntityListeners(AuditEntityListener.class)
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public abstract class TimestampedEntity {

    @Comment("Record creation timestamp set by DB trigger.")
    @Column(name = "CREATED_DAT", nullable = false, updatable = false, insertable = false)
    private OffsetDateTime createdDat;

    @Comment("Record last update timestamp set by DB trigger.")
    @Column(name = "LAST_UPDATE_DAT", nullable = false, updatable = false, insertable = false)
    private OffsetDateTime lastUpdateDat;
}

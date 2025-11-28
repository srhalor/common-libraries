package com.shdev.omsdatabase.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

/**
 * Base mapped superclass for entities capturing create and last update user identifiers.
 * Present on DocumentConfigEntity and ReferenceDataEntity.
 *
 * @author Shailesh Halor
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public abstract class SingleAuditUidEntity extends TimestampedEntity {

    @Comment("User ID who created the record.")
    @Column(name = "CREATE_UID", nullable = false, length = 20, updatable = false)
    private String createUid;

    @Comment("User ID who last updated the record.")
    @Column(name = "LAST_UPDATE_UID", nullable = false, length = 20)
    private String lastUpdateUid;
}


package com.shdev.omsdatabase.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

/**
 * Base mapped superclass for entities capturing header and token based create user identifiers.
 * Present on DocumentRequestEntity and ThBatchEntity.
 *
 * @author Shailesh Halor
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public abstract class DualCreateUidEntity extends TimestampedEntity {

    @Comment("User ID from request header when creating the record.")
    @Column(name = "CREATE_UID_HEADER", nullable = false, length = 20, updatable = false)
    private String createUidHeader;

    @Comment("User ID from JWT token when creating the record.")
    @Column(name = "CREATE_UID_TOKEN", nullable = false, length = 20, updatable = false)
    private String createUidToken;
}

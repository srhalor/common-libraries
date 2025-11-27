package com.shdev.omsdatabase.util;

import com.shdev.omsdatabase.entity.DocumentConfigEntity;
import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import com.shdev.omsdatabase.entity.ThBatchEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import static com.shdev.omsdatabase.util.OmsConstants.USER_ID_HEADER;
import static com.shdev.omsdatabase.util.OmsConstants.USER_ID_TOKEN;

/**
 * JPA lifecycle listener to populate audit user fields automatically on persist/update.
 * Looks up user identifiers from thread context (SLF4J MDC) using well-known keys.
 * <p>
 * Keys:
 * - userIdHeader -> CREATE_UID_HEADER
 * - userIdToken  -> CREATE_UID_TOKEN
 * - userId       -> CREATE_UID / LAST_UPDATE_UID
 * <p>
 * This keeps services free of audit boilerplate and avoids Spring dependencies in entities.
 *
 * @author Shailesh Halor
 */
@Slf4j
public class AuditEntityListener {

    /**
     * Helper to get value from MDC, treating blank as null
     *
     * @param key the MDC key
     * @return the value or null
     */
    private static String mdc(String key) {
        log.trace("MDC key: {}", key);
        var value = MDC.get(key);
        return StringUtils.hasText(value) ? value : null;

    }

    /**
     * Pre-persist lifecycle callback to set create user fields.
     *
     * @param entity the entity being persisted
     */
    @PrePersist
    public void prePersist(Object entity) {

        log.trace("Entering prePersist for entity: {}", entity.getClass().getSimpleName());

        applyHeaderAndTokenUidCreateFields(entity);
        applyGenericUidCreateAndUpdateFields(entity);

        log.trace("Exiting prePersist for entity: {}", entity.getClass().getSimpleName());
    }

    // Applies CREATE_UID_HEADER and CREATE_UID_TOKEN where supported entities are involved
    private static void applyHeaderAndTokenUidCreateFields(Object entity) {
        var headerUid = mdc(USER_ID_HEADER);
        var tokenUid = mdc(USER_ID_TOKEN);
        log.trace("prePersist Header UID: {}, Token UID: {}", headerUid, tokenUid);

        if (entity instanceof DocumentRequestEntity e) {
            if (null != headerUid && null == e.getCreateUidHeader()) e.setCreateUidHeader(headerUid);
            if (null != tokenUid && null == e.getCreateUidToken()) e.setCreateUidToken(tokenUid);
            return;
        }
        if (entity instanceof ThBatchEntity e) {
            if (null != headerUid && null == e.getCreateUidHeader()) e.setCreateUidHeader(headerUid);
            if (null != tokenUid && null == e.getCreateUidToken()) e.setCreateUidToken(tokenUid);
        }
    }

    // Applies generic CREATE_UID and LAST_UPDATE_UID for entities that use a single user identifier
    private static void applyGenericUidCreateAndUpdateFields(Object entity) {

        var genericUid = mdc(USER_ID_HEADER);
        log.trace("prePersist Generic UID: {}", genericUid);

        if (entity instanceof DocumentConfigEntity d) {
            if (null != genericUid && null == d.getCreateUid()) d.setCreateUid(genericUid);
            if (null != genericUid && null == d.getLastUpdateUid()) d.setLastUpdateUid(genericUid);
            return;
        }
        if (entity instanceof ReferenceDataEntity r) {
            if (null != genericUid && null == r.getCreateUid()) r.setCreateUid(genericUid);
            if (null != genericUid && null == r.getLastUpdateUid()) r.setLastUpdateUid(genericUid);
        }
    }

    /**
     * Pre-update lifecycle callback to set last update user fields.
     *
     * @param entity the entity being updated
     */
    @PreUpdate
    public void preUpdate(Object entity) {

        log.trace("Entering preUpdate for entity: {}", entity.getClass().getSimpleName());

        var genericUid = mdc(USER_ID_HEADER);
        log.trace("preUpdate Generic UID: {}", genericUid);

        if (null != genericUid) {

            if (entity instanceof DocumentConfigEntity d) {
                d.setLastUpdateUid(genericUid);
                return;
            }
            if (entity instanceof ReferenceDataEntity r) {
                r.setLastUpdateUid(genericUid);
            }
        }
        log.trace("Exiting preUpdate for entity: {}", entity.getClass().getSimpleName());
    }
}

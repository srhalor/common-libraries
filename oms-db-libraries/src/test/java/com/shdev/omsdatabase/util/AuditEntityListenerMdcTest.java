package com.shdev.omsdatabase.util;

import com.shdev.omsdatabase.entity.DocumentConfigEntity;
import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class AuditEntityListenerMdcTest {

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void prePersist_sets_header_and_token_from_mdc_for_DocumentRequest() {
        // no MDC -> no values set
        DocumentRequestEntity e = DocumentRequestEntity.builder().build();
        new AuditEntityListener().prePersist(e);
        assertNull(e.getCreateUidHeader());
        assertNull(e.getCreateUidToken());

        // with MDC
        MDC.put("userIdHeader", "hdrUser");
        MDC.put("userIdToken", "tokUser");
        DocumentRequestEntity e2 = DocumentRequestEntity.builder().build();
        new AuditEntityListener().prePersist(e2);
        assertEquals("hdrUser", e2.getCreateUidHeader());
        assertEquals("tokUser", e2.getCreateUidToken());
    }

    @Test
    void prePersist_and_preUpdate_set_generic_user_for_DocumentConfig() {
        // no MDC
        DocumentConfigEntity e = DocumentConfigEntity.builder().build();
        new AuditEntityListener().prePersist(e);
        assertNull(e.getCreateUid());
        assertNull(e.getLastUpdateUid());

        MDC.put("userId", "genericUser");
        DocumentConfigEntity e2 = DocumentConfigEntity.builder().build();
        new AuditEntityListener().prePersist(e2);
        assertEquals("genericUser", e2.getCreateUid());
        assertEquals("genericUser", e2.getLastUpdateUid());

        // update
        MDC.put("userId", "genericUser2");
        new AuditEntityListener().preUpdate(e2);
        assertEquals("genericUser2", e2.getLastUpdateUid());
    }
}


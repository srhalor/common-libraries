package com.shdev.omsdatabase.dto.request;

import com.shdev.omsdatabase.entity.DocumentRequestBlobEntity;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link DocumentRequestBlobEntity}
 *
 * @author Shailesh Halor
 */
public record DocumentRequestBlobReqDto(
        @NotNull
        Long omdrtId,
        @NotNull
        String jsonRequest,
        String xmlRequest) implements Serializable {
}

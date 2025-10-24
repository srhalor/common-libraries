package com.shdev.omsdatabase.dto.response;

import com.shdev.omsdatabase.entity.DocumentRequestBlobEntity;

import java.io.Serializable;

/**
 * DTO for {@link DocumentRequestBlobEntity}
 *
 * @author Shailesh Halor
 */
public record DocumentRequestBlobResDto(
        Long id,
        DocumentRequestResDto omdrt,
        String jsonRequest,
        String xmlRequest) implements Serializable {
}

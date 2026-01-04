package com.shdev.omsdatabase.dto;

/**
 * Document Content DTO for JSON/XML payload retrieval.
 * Used to return the actual document request content (JSON or XML format).
 *
 * @param requestId   the document request identifier
 * @param contentType the type of content (JSON or XML)
 * @param content     the actual content as string
 * @author Shailesh Halor
 */
public record DocumentContentDto(
        Long requestId,
        String contentType,
        String content
) {
}


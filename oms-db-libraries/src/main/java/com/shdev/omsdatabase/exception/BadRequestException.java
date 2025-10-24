package com.shdev.omsdatabase.exception;

/**
 * Thrown to indicate a client-side error such as invalid input or missing/invalid foreign keys.
 *
 * @author Shailesh Halor
 */
public class BadRequestException extends RuntimeException {

    /**
     * Construct a new BadRequestException with a human-readable message.
     *
     * @param message description of the client-side error
     */
    public BadRequestException(String message) {
        super(message);
    }
}

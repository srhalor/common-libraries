package com.shdev.omsdatabase.exception;

/**
 * Thrown when a requested entity cannot be found in the persistence store.
 *
 * @author Shailesh Halor
 */
public class NotFoundException extends RuntimeException {

    /**
     * Construct a new NotFoundException with a human-readable message.
     *
     * @param message description of the missing resource (e.g., entity and id)
     */
    public NotFoundException(String message) {
        super(message);
    }
}

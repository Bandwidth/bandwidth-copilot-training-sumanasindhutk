package com.journal.exception;

/**
 * Custom exception for journal storage operations.
 * Thrown when file system operations fail (read, write, delete).
 */
public class JournalStorageException extends RuntimeException {

    /**
     * Creates a new JournalStorageException with a message.
     *
     * @param message The error message
     */
    public JournalStorageException(String message) {
        super(message);
    }

    /**
     * Creates a new JournalStorageException with a message and cause.
     *
     * @param message The error message
     * @param cause The underlying cause
     */
    public JournalStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

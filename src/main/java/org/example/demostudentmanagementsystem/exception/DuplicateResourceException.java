package org.example.demostudentmanagementsystem.exception;

/**
 * Exception thrown when attempting to create a resource that already exists.
 */
public class DuplicateResourceException extends RuntimeException {

    private final String errorCode;

    /**
     * Constructs a new DuplicateResourceException with a detail message and error code.
     *
     * @param message   The error message
     * @param errorCode The specific error code
     */
    public DuplicateResourceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new DuplicateResourceException with a detail message.
     *
     * @param message The error message
     */
    public DuplicateResourceException(String message) {
        super(message);
        this.errorCode = "RESOURCE_ALREADY_EXISTS";
    }

    /**
     * Returns the associated error code.
     *
     * @return Error code string
     */
    public String getErrorCode() {
        return errorCode;
    }
}

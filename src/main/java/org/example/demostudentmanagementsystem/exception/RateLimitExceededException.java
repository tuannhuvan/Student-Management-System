package org.example.demostudentmanagementsystem.exception;

/**
 * Exception thrown when a client exceeds the allowed API rate limit.
 */
public class RateLimitExceededException extends RuntimeException {

    private final String errorCode;

    /**
     * Constructs a new RateLimitExceededException with a detail message.
     *
     * @param message The detail message
     */
    public RateLimitExceededException(String message) {
        super(message);
        this.errorCode = "AUTH_RATE_LIMIT_EXCEEDED";
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

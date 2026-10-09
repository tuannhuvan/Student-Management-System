package org.example.demostudentmanagementsystem.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler providing RFC 7807 Problem Detail responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles validation errors when request body fails Bean Validation rules.
     *
     * @param ex MethodArgumentNotValidException instance
     * @return ProblemDetail with HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed for request parameters");
        problem.setTitle("Bad Request");
        problem.setProperty("errorCode", "AUTH_VALIDATION_FAILED");
        problem.setProperty("timestamp", Instant.now());

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problem.setProperty("errors", fieldErrors);
        return problem;
    }

    /**
     * Handles malformed JSON payload errors.
     *
     * @param ex HttpMessageNotReadableException instance
     * @return ProblemDetail with HTTP 400 status
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Malformed JSON request body");
        problem.setTitle("Bad Request");
        problem.setProperty("errorCode", "AUTH_VALIDATION_FAILED");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles duplicate resource conflicts (e.g. email or username already exists).
     *
     * @param ex DuplicateResourceException instance
     * @return ProblemDetail with HTTP 409 status
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResourceException(DuplicateResourceException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Resource Conflict");
        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles rate limit exceeded exceptions.
     *
     * @param ex RateLimitExceededException instance
     * @return ProblemDetail with HTTP 429 status
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ProblemDetail handleRateLimitExceededException(RateLimitExceededException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
        problem.setTitle("Too Many Requests");
        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles student not found exception.
     *
     * @param ex StudentNotFoundException instance
     * @return ProblemDetail with HTTP 404 status
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail handleStudentNotFoundException(StudentNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Student Not Found");
        problem.setProperty("errorCode", "STUDENT_NOT_FOUND");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles course not found exception.
     *
     * @param ex CourseNotFoundException instance
     * @return ProblemDetail with HTTP 404 status
     */
    @ExceptionHandler(CourseNotFoundException.class)
    public ProblemDetail handleCourseNotFoundException(CourseNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Course Not Found");
        problem.setProperty("errorCode", "COURSE_NOT_FOUND");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /**
     * Handles unhandled runtime and system exceptions.
     *
     * @param ex Exception instance
     * @return ProblemDetail with HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex) {
        log.error("Unhandled exception in application: ", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problem.setTitle("Internal Server Error");
        problem.setProperty("errorCode", "INTERNAL_SERVER_ERROR");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}

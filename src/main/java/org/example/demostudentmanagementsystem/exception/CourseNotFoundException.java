package org.example.demostudentmanagementsystem.exception;

/**
 * Exception thrown when a requested course entity is not found in the system.
 */
public class CourseNotFoundException extends RuntimeException {

    /**
     * Constructs a new CourseNotFoundException with the specified course ID.
     *
     * @param id Course ID
     */
    public CourseNotFoundException(Long id) {
        super("Course not found with ID: " + id);
    }
}

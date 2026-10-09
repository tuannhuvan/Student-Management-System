package org.example.demostudentmanagementsystem.exception;

/**
 * Exception thrown when a requested student entity is not found in the system.
 */
public class StudentNotFoundException extends RuntimeException {

    /**
     * Constructs a new StudentNotFoundException with the specified student ID.
     *
     * @param id Student ID
     */
    public StudentNotFoundException(Long id) {
        super("Student not found with ID: " + id);
    }
}

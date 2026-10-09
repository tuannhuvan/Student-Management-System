package org.example.demostudentmanagementsystem.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO record representing a grade submission request by an instructor.
 *
 * @param studentId ID of the student
 * @param courseId  ID of the course
 * @param score     Numeric score percentage (0.0 to 100.0)
 */
public record GradeSubmissionRequest(
    @NotNull(message = "Student ID is required")
    Long studentId,

    @NotNull(message = "Course ID is required")
    Long courseId,

    @NotNull(message = "Grade score is required")
    @DecimalMin(value = "0.0", message = "Score must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Score must not exceed 100.0")
    BigDecimal score
) {
}

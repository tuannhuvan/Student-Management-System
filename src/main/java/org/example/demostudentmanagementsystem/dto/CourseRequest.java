package org.example.demostudentmanagementsystem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO record representing course creation request payload.
 *
 * @param code    Course code (e.g. CS101)
 * @param title   Course title
 * @param credits Number of credit units (1-6)
 */
public record CourseRequest(
    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code must not exceed 20 characters")
    String code,

    @NotBlank(message = "Course title is required")
    @Size(max = 100, message = "Course title must not exceed 100 characters")
    String title,

    @NotNull(message = "Credits value is required")
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 6, message = "Credits must not exceed 6")
    Integer credits
) {
}

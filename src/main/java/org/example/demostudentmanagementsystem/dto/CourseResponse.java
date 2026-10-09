package org.example.demostudentmanagementsystem.dto;

import java.time.Instant;

/**
 * DTO record representing course response payload.
 *
 * @param id        Unique course ID
 * @param code      Course code
 * @param title     Course title
 * @param credits   Credit units
 * @param createdAt Creation timestamp
 */
public record CourseResponse(
    Long id,
    String code,
    String title,
    Integer credits,
    Instant createdAt
) {
}

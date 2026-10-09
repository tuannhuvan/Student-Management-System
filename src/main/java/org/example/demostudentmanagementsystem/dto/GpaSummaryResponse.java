package org.example.demostudentmanagementsystem.dto;

import java.math.BigDecimal;

/**
 * DTO record representing calculated GPA summary and academic standing.
 *
 * @param studentId      ID of the student
 * @param studentName    Full name of the student
 * @param gpa            Cumulative GPA (0.00 to 4.00)
 * @param totalCredits   Total completed credit units
 * @param academicStatus Academic standing status (Dean's List, Good Standing, Academic Warning, Academic Suspension)
 */
public record GpaSummaryResponse(
    Long studentId,
    String studentName,
    BigDecimal gpa,
    Integer totalCredits,
    String academicStatus
) {
}

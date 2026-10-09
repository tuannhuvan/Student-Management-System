package org.example.demostudentmanagementsystem.audit;

import org.example.demostudentmanagementsystem.entity.LetterGrade;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Domain event published when a course grade is submitted or updated for a student.
 *
 * @param eventId     Unique event identifier
 * @param studentId   ID of the student
 * @param courseId    ID of the course
 * @param score       Numeric score percentage
 * @param letterGrade Calculated LetterGrade enum
 * @param timestamp   Event creation timestamp
 */
public record GradeRecordedEvent(
    String eventId,
    Long studentId,
    Long courseId,
    BigDecimal score,
    LetterGrade letterGrade,
    Instant timestamp
) {
}

package org.example.demostudentmanagementsystem.audit;

import java.time.Instant;

/**
 * Domain event published when a new student profile is created.
 *
 * @param eventId   Unique event identifier
 * @param studentId ID of the created student
 * @param email     Email of the student
 * @param timestamp Event creation timestamp
 */
public record StudentCreatedEvent(
    String eventId,
    Long studentId,
    String email,
    Instant timestamp
) {
}

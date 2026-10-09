package org.example.demostudentmanagementsystem.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener responsible for logging audit trails when domain events occur.
 */
@Component
@Slf4j
public class AuditEventListener {

    /**
     * Handles {@link UserRegisteredEvent} asynchronously to log user registration audit records.
     *
     * @param event The published registration event
     */
    @Async
    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("[AUDIT] User registered successfully - ID: {}, Username: {}, Email: {}, Time: {}",
                event.userId(), event.username(), event.email(), event.timestamp());
    }

    /**
     * Handles {@link StudentCreatedEvent} asynchronously to log student creation audit records.
     *
     * @param event The published student creation event
     */
    @Async
    @EventListener
    public void handleStudentCreatedEvent(StudentCreatedEvent event) {
        log.info("[AUDIT] Student profile created - EventID: {}, StudentID: {}, Email: {}, Time: {}",
                event.eventId(), event.studentId(), event.email(), event.timestamp());
    }

    /**
     * Handles {@link GradeRecordedEvent} asynchronously to log grade recording audit records.
     *
     * @param event The published grade recorded event
     */
    @Async
    @EventListener
    public void handleGradeRecordedEvent(GradeRecordedEvent event) {
        log.info("[AUDIT] Grade recorded - EventID: {}, StudentID: {}, CourseID: {}, Score: {}, Grade: {}, Time: {}",
                event.eventId(), event.studentId(), event.courseId(), event.score(), event.letterGrade(), event.timestamp());
    }
}

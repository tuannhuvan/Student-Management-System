package org.example.demostudentmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demostudentmanagementsystem.audit.GradeRecordedEvent;
import org.example.demostudentmanagementsystem.dto.GpaSummaryResponse;
import org.example.demostudentmanagementsystem.dto.GradeSubmissionRequest;
import org.example.demostudentmanagementsystem.entity.Course;
import org.example.demostudentmanagementsystem.entity.Enrollment;
import org.example.demostudentmanagementsystem.entity.LetterGrade;
import org.example.demostudentmanagementsystem.entity.Student;
import org.example.demostudentmanagementsystem.exception.CourseNotFoundException;
import org.example.demostudentmanagementsystem.exception.StudentNotFoundException;
import org.example.demostudentmanagementsystem.repository.CourseRepository;
import org.example.demostudentmanagementsystem.repository.EnrollmentRepository;
import org.example.demostudentmanagementsystem.repository.StudentRepository;
import org.example.demostudentmanagementsystem.service.GradeService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation handling grade submission, GPA computation, and academic alert evaluation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GradeServiceImpl implements GradeService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void submitGrade(GradeSubmissionRequest request) {
        log.info("Submitting grade for student ID: {}, course ID: {}, score: {}",
                request.studentId(), request.courseId(), request.score());

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new StudentNotFoundException(request.studentId()));

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new CourseNotFoundException(request.courseId()));

        LetterGrade letterGrade = LetterGrade.fromScore(request.score());

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElseGet(() -> Enrollment.builder()
                        .student(student)
                        .course(course)
                        .enrollmentDate(LocalDate.now())
                        .build());

        enrollment.setGradeScore(request.score());
        enrollment.setLetterGrade(letterGrade);

        enrollmentRepository.save(enrollment);
        log.info("Grade saved successfully for student ID: {}, letter grade: {}", student.getId(), letterGrade);

        eventPublisher.publishEvent(new GradeRecordedEvent(
                "evt_" + UUID.randomUUID().toString().substring(0, 8),
                student.getId(),
                course.getId(),
                request.score(),
                letterGrade,
                Instant.now()
        ));
    }

    @Override
    public GpaSummaryResponse getStudentGpaSummary(Long studentId) {
        log.info("Computing GPA summary for student ID: {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        if (enrollments.isEmpty()) {
            return new GpaSummaryResponse(
                    student.getId(),
                    student.getFirstName() + " " + student.getLastName(),
                    BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                    0,
                    "No Graded Courses"
            );
        }

        BigDecimal totalWeightedPoints = BigDecimal.ZERO;
        int totalCredits = 0;

        for (Enrollment enrollment : enrollments) {
            if (enrollment.getLetterGrade() != null && enrollment.getCourse() != null) {
                int credits = enrollment.getCourse().getCredits();
                BigDecimal points = enrollment.getLetterGrade().getGradePoints();
                BigDecimal weightedPoints = points.multiply(BigDecimal.valueOf(credits));

                totalWeightedPoints = totalWeightedPoints.add(weightedPoints);
                totalCredits += credits;
            }
        }

        BigDecimal gpa = BigDecimal.ZERO;
        if (totalCredits > 0) {
            gpa = totalWeightedPoints.divide(BigDecimal.valueOf(totalCredits), 2, RoundingMode.HALF_UP);
        }

        String academicStatus = evaluateAcademicStatus(gpa, totalCredits);

        return new GpaSummaryResponse(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                gpa,
                totalCredits,
                academicStatus
        );
    }

    private String evaluateAcademicStatus(BigDecimal gpa, int totalCredits) {
        double gpaVal = gpa.doubleValue();
        if (gpaVal >= 3.50 && totalCredits >= 12) {
            return "Dean's List";
        } else if (gpaVal < 1.50) {
            return "Academic Suspension Warning";
        } else if (gpaVal < 2.00) {
            return "Academic Warning";
        } else {
            return "Good Standing";
        }
    }
}

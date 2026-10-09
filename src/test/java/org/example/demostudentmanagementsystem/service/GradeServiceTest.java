package org.example.demostudentmanagementsystem.service;

import org.example.demostudentmanagementsystem.audit.GradeRecordedEvent;
import org.example.demostudentmanagementsystem.dto.GpaSummaryResponse;
import org.example.demostudentmanagementsystem.dto.GradeSubmissionRequest;
import org.example.demostudentmanagementsystem.entity.Course;
import org.example.demostudentmanagementsystem.entity.Enrollment;
import org.example.demostudentmanagementsystem.entity.LetterGrade;
import org.example.demostudentmanagementsystem.entity.Student;
import org.example.demostudentmanagementsystem.repository.CourseRepository;
import org.example.demostudentmanagementsystem.repository.EnrollmentRepository;
import org.example.demostudentmanagementsystem.repository.StudentRepository;
import org.example.demostudentmanagementsystem.service.impl.GradeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private GradeServiceImpl gradeService;

    private Student student;
    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@university.edu")
                .department("CS")
                .build();

        course1 = Course.builder().id(10L).code("CS101").title("Intro to CS").credits(4).build();
        course2 = Course.builder().id(20L).code("CS102").title("Data Structures").credits(4).build();
    }

    @Test
    @DisplayName("Should successfully submit grade and trigger event")
    void submitGrade_ValidRequest_Success() {
        GradeSubmissionRequest request = new GradeSubmissionRequest(1L, 10L, new BigDecimal("95.0"));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course1));
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 10L)).thenReturn(Optional.empty());

        gradeService.submitGrade(request);

        verify(enrollmentRepository).save(any(Enrollment.class));
        verify(eventPublisher).publishEvent(any(GradeRecordedEvent.class));
    }

    @Test
    @DisplayName("Should correctly calculate weighted Cumulative GPA and Dean's List status")
    void getStudentGpaSummary_CalculatesWeightedGpaAndDeansList() {
        Enrollment e1 = Enrollment.builder().student(student).course(course1).gradeScore(new BigDecimal("92.0")).letterGrade(LetterGrade.A).build(); // 4 credits * 4.0 = 16
        Course course3 = Course.builder().id(30L).code("CS103").title("Algorithms").credits(4).build();
        Enrollment e2 = Enrollment.builder().student(student).course(course2).gradeScore(new BigDecimal("85.0")).letterGrade(LetterGrade.B).build(); // 4 credits * 3.0 = 12
        Enrollment e3 = Enrollment.builder().student(student).course(course3).gradeScore(new BigDecimal("90.0")).letterGrade(LetterGrade.A).build(); // 4 credits * 4.0 = 16

        // Total weighted = 16 + 12 + 16 = 44. Total credits = 12. GPA = 44/12 = 3.67
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(enrollmentRepository.findByStudentId(1L)).thenReturn(List.of(e1, e2, e3));

        GpaSummaryResponse summary = gradeService.getStudentGpaSummary(1L);

        assertThat(summary).isNotNull();
        assertThat(summary.gpa()).isEqualTo(new BigDecimal("3.67"));
        assertThat(summary.totalCredits()).isEqualTo(12);
        assertThat(summary.academicStatus()).isEqualTo("Dean's List");
    }
}

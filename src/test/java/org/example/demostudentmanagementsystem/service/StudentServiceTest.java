package org.example.demostudentmanagementsystem.service;

import org.example.demostudentmanagementsystem.audit.StudentCreatedEvent;
import org.example.demostudentmanagementsystem.dto.StudentRequest;
import org.example.demostudentmanagementsystem.dto.StudentResponse;
import org.example.demostudentmanagementsystem.entity.Student;
import org.example.demostudentmanagementsystem.exception.DuplicateResourceException;
import org.example.demostudentmanagementsystem.exception.StudentNotFoundException;
import org.example.demostudentmanagementsystem.repository.StudentRepository;
import org.example.demostudentmanagementsystem.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private StudentServiceImpl studentService;

    private StudentRequest studentRequest;
    private Student student;

    @BeforeEach
    void setUp() {
        studentRequest = new StudentRequest(
                "Alex",
                "Rivera",
                "alex.rivera@university.edu",
                "Computer Science",
                LocalDate.of(2002, 5, 15)
        );

        student = Student.builder()
                .id(105L)
                .firstName("Alex")
                .lastName("Rivera")
                .email("alex.rivera@university.edu")
                .department("Computer Science")
                .dob(LocalDate.of(2002, 5, 15))
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should successfully create a new student record")
    void createStudent_ValidRequest_Success() {
        when(studentRepository.existsByEmail(studentRequest.email())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponse response = studentService.createStudent(studentRequest);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(105L);
        assertThat(response.email()).isEqualTo("alex.rivera@university.edu");
        verify(studentRepository).save(any(Student.class));
        verify(eventPublisher).publishEvent(any(StudentCreatedEvent.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when creating student with existing email")
    void createStudent_DuplicateEmail_ThrowsException() {
        when(studentRepository.existsByEmail(studentRequest.email())).thenReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(studentRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Student email already exists");

        verify(studentRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Should retrieve student by ID")
    void getStudentById_ExistingId_ReturnsStudentResponse() {
        when(studentRepository.findById(105L)).thenReturn(Optional.of(student));

        StudentResponse response = studentService.getStudentById(105L);

        assertThat(response).isNotNull();
        assertThat(response.firstName()).isEqualTo("Alex");
    }

    @Test
    @DisplayName("Should throw StudentNotFoundException when fetching non-existing ID")
    void getStudentById_NonExistingId_ThrowsException() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(999L))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    @DisplayName("Should search students with keyword pagination")
    void getAllStudents_WithKeyword_ReturnsPage() {
        Page<Student> page = new PageImpl<>(List.of(student));
        when(studentRepository.searchStudents(any(), any(Pageable.class))).thenReturn(page);

        Page<StudentResponse> result = studentService.getAllStudents(0, 10, "id,asc", "Alex");

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should delete student by ID")
    void deleteStudent_ExistingId_Success() {
        when(studentRepository.existsById(105L)).thenReturn(true);

        studentService.deleteStudent(105L);

        verify(studentRepository).deleteById(105L);
    }
}

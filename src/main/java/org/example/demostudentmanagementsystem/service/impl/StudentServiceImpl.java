package org.example.demostudentmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demostudentmanagementsystem.audit.StudentCreatedEvent;
import org.example.demostudentmanagementsystem.dto.StudentRequest;
import org.example.demostudentmanagementsystem.dto.StudentResponse;
import org.example.demostudentmanagementsystem.entity.Student;
import org.example.demostudentmanagementsystem.exception.DuplicateResourceException;
import org.example.demostudentmanagementsystem.exception.StudentNotFoundException;
import org.example.demostudentmanagementsystem.repository.StudentRepository;
import org.example.demostudentmanagementsystem.service.StudentService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service implementation handling student management business operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        log.info("Creating student with email: {}", request.email());

        if (studentRepository.existsByEmail(request.email())) {
            log.warn("Student creation failed: Email {} already exists", request.email());
            throw new DuplicateResourceException("Student email already exists", "STUDENT_EMAIL_ALREADY_EXISTS");
        }

        Student student = Student.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .department(request.department())
                .dob(request.dob())
                .build();

        Student savedStudent = studentRepository.save(student);
        log.info("Student created successfully with ID: {}", savedStudent.getId());

        eventPublisher.publishEvent(new StudentCreatedEvent(
                "evt_" + UUID.randomUUID().toString().substring(0, 8),
                savedStudent.getId(),
                savedStudent.getEmail(),
                Instant.now()
        ));

        return mapToResponse(savedStudent);
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        log.info("Fetching student details for ID: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        return mapToResponse(student);
    }

    @Override
    public Page<StudentResponse> getAllStudents(int page, int size, String sort, String keyword) {
        log.info("Fetching students page: {}, size: {}, sort: {}, keyword: {}", page, size, sort, keyword);
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<Student> studentPage;
        if (keyword != null && !keyword.isBlank()) {
            studentPage = studentRepository.searchStudents(keyword.trim(), pageable);
        } else {
            studentPage = studentRepository.findAll(pageable);
        }

        return studentPage.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        log.info("Updating student ID: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        if (!student.getEmail().equalsIgnoreCase(request.email()) && studentRepository.existsByEmail(request.email())) {
            log.warn("Update failed: Email {} already in use by another student", request.email());
            throw new DuplicateResourceException("Student email already exists", "STUDENT_EMAIL_ALREADY_EXISTS");
        }

        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        student.setDepartment(request.department());
        student.setDob(request.dob());

        Student updatedStudent = studentRepository.save(student);
        log.info("Student ID {} updated successfully", updatedStudent.getId());
        return mapToResponse(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        log.info("Deleting student ID: {}", id);
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
        log.info("Student ID {} deleted successfully", id);
    }

    private StudentResponse mapToResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getDepartment(),
                student.getDob(),
                student.getCreatedAt()
        );
    }
}

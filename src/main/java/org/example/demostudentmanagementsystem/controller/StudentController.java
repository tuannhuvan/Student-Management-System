package org.example.demostudentmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demostudentmanagementsystem.dto.StudentRequest;
import org.example.demostudentmanagementsystem.dto.StudentResponse;
import org.example.demostudentmanagementsystem.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * REST controller for student CRUD operations and search filtering.
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * Creates a new student profile.
     *
     * @param request Validated student request payload
     * @return ResponseEntity with HTTP 201 Created status and student details
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        StudentResponse response = studentService.createStudent(request);
        URI location = URI.create("/api/v1/students/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Retrieves student details by ID.
     *
     * @param id Student ID
     * @return Student details DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or hasRole('STUDENT')")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves paginated and searchable list of students.
     *
     * @param page    Page index (0-based)
     * @param size    Page size
     * @param sort    Sort field and direction
     * @param keyword Search keyword
     * @return Page of StudentResponse DTOs
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<Page<StudentResponse>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) String keyword
    ) {
        Page<StudentResponse> response = studentService.getAllStudents(page, size, sort, keyword);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing student profile.
     *
     * @param id      Student ID
     * @param request Validated updated student payload
     * @return Updated student DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Removes a student profile by ID. Requires ROLE_ADMIN.
     *
     * @param id Student ID
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}

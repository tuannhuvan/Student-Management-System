package org.example.demostudentmanagementsystem.repository;

import org.example.demostudentmanagementsystem.entity.Enrollment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Enrollment} entity operations.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Finds all enrollments for a specific student, fetching associated courses eagerly.
     *
     * @param studentId Student ID
     * @return List of enrollment entities
     */
    @EntityGraph(attributePaths = {"course"})
    List<Enrollment> findByStudentId(Long studentId);

    /**
     * Finds an enrollment by student ID and course ID.
     *
     * @param studentId Student ID
     * @param courseId  Course ID
     * @return Optional containing enrollment if found
     */
    @EntityGraph(attributePaths = {"student", "course"})
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Checks if an enrollment exists for a student in a specific course.
     *
     * @param studentId Student ID
     * @param courseId  Course ID
     * @return true if enrollment exists
     */
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}

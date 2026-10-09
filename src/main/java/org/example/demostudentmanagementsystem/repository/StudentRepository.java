package org.example.demostudentmanagementsystem.repository;

import org.example.demostudentmanagementsystem.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link Student} entity operations.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Checks if a student exists with the given email address.
     *
     * @param email Email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Finds a student by email address ignoring case sensitivity.
     *
     * @param email Student email
     * @return Optional containing student if found
     */
    Optional<Student> findByEmailIgnoreCase(String email);

    /**
     * Searches students by keyword matching first name, last name, or email with pagination.
     * Uses @EntityGraph to eagerly fetch enrollments preventing N+1 queries.
     *
     * @param keyword  Search keyword
     * @param pageable Pagination parameters
     * @return Page of matching Student entities
     */
    @EntityGraph(attributePaths = {"enrollments"})
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Student> searchStudents(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Finds student details with enrollments eagerly fetched by ID.
     *
     * @param id Student ID
     * @return Optional containing student with enrollments
     */
    @EntityGraph(attributePaths = {"enrollments"})
    @Query("SELECT s FROM Student s WHERE s.id = :id")
    Optional<Student> findByIdWithEnrollments(@Param("id") Long id);
}

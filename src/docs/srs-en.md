# Software Requirements Specification (SRS)
**Project Title:** Student Management System (Spring Boot 3.x CRUD Application)  
**Framework:** Spring Boot 3.x & Spring Framework 6.x  
**Document Language:** English  
**Version:** 1.0  

---

## 1. Introduction

### 1.1 Purpose
This Software Requirements Specification (SRS) document details the functional and non-functional requirements, system architecture, data constraints, and deployment standards for the **Student Management System**. It serves as the primary reference for development, testing, and system delivery.

### 1.2 Scope
The system provides a robust, production-ready RESTful API backend delivering complete CRUD (Create, Read, Update, Delete) capabilities, pagination, search, validation boundaries, centralized global exception handling, and Docker containerization based on **Java 17+** and **Spring Boot 3.x**.

### 1.3 Definitions, Acronyms, and Abbreviations
- **CRUD:** Create, Read, Update, Delete.
- **DTO:** Data Transfer Object.
- **JPA:** Jakarta Persistence API.
- **IoC / DI:** Inversion of Control / Dependency Injection.
- **RFC 7807:** Problem Details for HTTP APIs Specification.
- **AOT:** Ahead-Of-Time compilation (GraalVM Native Image).

---

## 2. Overall Description

### 2.1 Product Perspective
The Student Management System functions as a standalone backend microservice communicating with client applications (Web, Mobile, Postman) over HTTP/JSON protocols.

### 2.2 User Classes and Characteristics
- **API Clients / Frontend Developers:** Consume REST endpoints for student records and domain associations.
- **DevOps / System Administrators:** Responsible for application deployment, monitoring via Actuator, and container orchestration.

### 2.3 Operating Environment
- **Java Runtime:** OpenJDK / Eclipse Temurin 17 or higher.
- **Database System:** MySQL 8.0 (Production) / H2 Database (Development).
- **Container Runtime:** Docker Engine & Docker Compose.

---

## 3. System Features & Functional Requirements (FR)

### FR-1: Student Management (CRUD Operations)
- **FR-1.1 (Create Student):** Allows clients to register a new student with `firstName`, `lastName`, `email`, and `dob`.
  - *Constraint:* Email must be unique (`unique = true`). Duplicate email requests must be rejected with a business exception.
- **FR-1.2 (List Students with Pagination):** Retrieves paginated student records using `Pageable` parameters (`page`, `size`).
- **FR-1.3 (Get Student by ID):** Returns detailed student information by ID or returns `404 Not Found` if nonexistent.
- **FR-1.4 (Update Student):** Updates existing student records by ID.
- **FR-1.5 (Delete Student):** Removes a student from the database by ID and returns `204 No Content`.

### FR-2: Search & Query Capabilities
- **FR-2.1:** Case-insensitive search by exact email matching (`findByEmailIgnoreCase`).
- **FR-2.2:** Dynamic keyword search against first name or last name using JPQL `LIKE` patterns.

### FR-3: Input Data Validation Boundary
- **FR-3.1:** `firstName` and `lastName` must not be blank (`@NotBlank`).
- **FR-3.2:** `email` must be a valid email format (`@Email`) and not blank (`@NotBlank`).
- **FR-3.3:** `dob` must be a date in the past or present (`@PastOrPresent`).

### FR-4: Entity Relationship Management
- **FR-4.1 (1:1 Relationship):** `Author` to `AuthorProfile` with `CascadeType.ALL` and `orphanRemoval = true`.
- **FR-4.2 (1:N / N:1 Relationship):** `Publisher` to `Book` using `@ManyToOne(fetch = FetchType.LAZY)`.
- **FR-4.3 (N:N Relationship):** `Book` to `Author` via join table `book_authors` backed by Java `Set<T>`.

### FR-5: Centralized Exception Handling
- **FR-5.1:** Intercepts all controller layer exceptions using `@RestControllerAdvice`.
- **FR-5.2:** Generates standardized JSON error payloads conforming to RFC 7807 containing `timestamp`, `status`, `error`, and field `details`.

---

## 4. Non-Functional Requirements (NFR)

### NFR-1: Performance & Data Access Optimization
- **NFR-1.1:** Average REST API response time must remain $< 200	ext{ ms}$.
- **NFR-1.2:** Eliminate the **N+1 Query Problem** using `JOIN FETCH` or `@EntityGraph`.
- **NFR-1.3:** Disable Open Session in View (`spring.jpa.open-in-view=false`) to enforce clean transaction boundaries.

### NFR-2: Security & Data Encapsulation
- **NFR-2.1:** Decouple public API contracts from internal JPA Entities using immutable Java 17 Records (`StudentRequest`, `StudentResponse`).
- **NFR-2.2:** Externalize sensitive database credentials via environment variables.

### NFR-3: Maintainability & Architectural Standards
- **NFR-3.1:** Enforce Constructor Injection over Field Injection (`@Autowired`) to ensure immutability and testability.
- **NFR-3.2:** Strict adherence to Layered Architecture (Controller -> Service -> Repository -> Entity).

### NFR-4: Portability & Deployment
- **NFR-4.1:** Fully containerized via a multi-stage Alpine JRE Dockerfile.
- **NFR-4.2:** Cloud-native observability enabled via Spring Boot Actuator and Micrometer.

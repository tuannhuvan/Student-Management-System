# Database Schema & Data Modeling Specification

Đặc tả Thiết kế Cơ sở Dữ liệu Quan hệ (Relational DB Schema) cho Hệ thống Quản lý Sinh viên.

---

## 1. Sơ đồ Quan hệ Thực thể (ERD Diagram Textual representation)

```
[ DEPARTMENTS ] (1) ────< (N) [ STUDENTS ] (1) ────< (N) [ ENROLLMENTS ] (N) >──── (1) [ COURSES ]
                                    │
                                   (1)
                                    │
                                   (1)
                             [ PROFILES ]
```

---

## 2. Chi tiết Bảng và Trường Dữ liệu (Tables & Fields Specification)

### 2.1. Bảng `departments` (Khoa / Ngành học)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | ID định danh |
| `code` | VARCHAR(20) | UNIQUE, NOT NULL | Mã khoa (VD: `CNTT`, `DTVT`) |
| `name` | VARCHAR(100) | NOT NULL | Tên khoa |
| `created_at` | DATETIME | NOT NULL | Thời gian tạo |

### 2.2. Bảng `students` (Sinh viên)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | ID định danh |
| `student_code` | VARCHAR(20) | UNIQUE, NOT NULL, INDEX | Mã sinh viên (VD: `SV2026001`) |
| `full_name` | VARCHAR(100) | NOT NULL | Họ và tên |
| `email` | VARCHAR(100) | UNIQUE, NOT NULL, INDEX | Email sinh viên |
| `date_of_birth` | DATE | NOT NULL | Ngày sinh |
| `department_id` | BIGINT | FOREIGN KEY (`departments.id`) | Mã khoa phụ thuộc |
| `status` | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' | Trạng thái (ACTIVE, SUSPENDED, GRADUATED) |
| `created_at` | DATETIME | NOT NULL | Thời gian tạo |
| `updated_at` | DATETIME | NULL | Thời gian cập nhật |

### 2.3. Bảng `courses` (Môn học)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | ID định danh môn học |
| `course_code` | VARCHAR(20) | UNIQUE, NOT NULL | Mã môn học (VD: `JAVA101`) |
| `title` | VARCHAR(150) | NOT NULL | Tên môn học |
| `credits` | INT | NOT NULL, CHECK (`credits` > 0) | Số tín chỉ |

### 2.4. Bảng `enrollments` (Đăng ký học & Điểm số)
| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | ID đăng ký |
| `student_id` | BIGINT | FOREIGN KEY (`students.id`), INDEX | Foreign key sinh viên |
| `course_id` | BIGINT | FOREIGN KEY (`courses.id`), INDEX | Foreign key môn học |
| `midterm_score` | DECIMAL(4,2) | NULL, CHECK (0 <= score <= 10) | Điểm giữa kỳ |
| `final_score` | DECIMAL(4,2) | NULL, CHECK (0 <= score <= 10) | Điểm cuối kỳ |
| `total_score` | DECIMAL(4,2) | NULL, CHECK (0 <= score <= 10) | Điểm tổng kết |
| `grade_letter` | VARCHAR(2) | NULL | Điểm chữ (A, B, C, D, F) |

---

## 3. Chỉ mục & Tối ưu hóa (Indexes & Performance Tuning)

- **Composite Index:** `CREATE INDEX idx_student_dept_status ON students(department_id, status);`
- **Search Index:** `CREATE INDEX idx_student_email_code ON students(email, student_code);`
- **Unique Constraint:** `ALTER TABLE enrollments ADD CONSTRAINT uq_student_course UNIQUE (student_id, course_id);`

# Tài liệu Kiến trúc Nghiệp vụ & Kiến trúc Kỹ thuật (Business & System Architecture)
**Dự án:** Student Management System (Spring Boot 3.x CRUD)  
**Tài liệu tham chiếu:** Spring Framework 6.x & Spring Boot 3.x CRUD Application Guide  

---

## 1. Tổng quan Kiến trúc Hệ thống (System Architecture Overview)

Hệ thống được thiết kế theo **Kiến trúc Phân tầng (Layered Architecture)** tiêu chuẩn ngành phần mềm, đảm bảo nguyên lý **Separation of Concerns (SoC)**, tăng tính đóng gói, dễ bảo trì và mở rộng.

```
+-----------------------------------------------------------------------+
|                         Client Layer                                  |
|               (Browser / Postman / Mobile / Swagger UI)                |
+-----------------------------------------------------------------------+
                                  | HTTP Request (JSON)
                                  v
+-----------------------------------------------------------------------+
|                     Controller Layer (@RestController)                |
|           Request Handling, DTO Mapping, Input Validation (@Valid)    |
+-----------------------------------------------------------------------+
                                  | DTO
                                  v
+-----------------------------------------------------------------------+
|                       Service Layer (@Service)                        |
|        Business Logic Execution, Transaction Management (@Transactional)|
+-----------------------------------------------------------------------+
                                  | Entity
                                  v
+-----------------------------------------------------------------------+
|                    Repository Layer (JpaRepository)                   |
|              Data Access Objects, JPQL, Spring Data JPA               |
+-----------------------------------------------------------------------+
                                  | SQL
                                  v
+-----------------------------------------------------------------------+
|                        Database Layer (MySQL / H2)                    |
|                        Persistent Relational Tables                   |
+-----------------------------------------------------------------------+
```

### Nguyên lý Đảo ngược Phụ thuộc (IoC) & Injection Pattern
1. **Spring IoC Container:** Quản lý vòng đời và sự phụ thuộc của các Java Objects (Beans) thông qua `ApplicationContext`.
2. **Constructor Injection (Recommended):**
   - Đảm bảo thuộc tính phụ thuộc là `final` (Promotes Immutability).
   - Loại bỏ nguy cơ `NullPointerException`.
   - Dễ dàng khởi tạo Mock objects trong Unit Test mà không cần Spring Container.
   - Tránh việc lạm dụng Field Injection (`@Autowired` trên thuộc tính).

---

## 2. Kiến trúc Nghiệp vụ & Mô hình Dữ liệu Domain (Business Architecture & Domain Model)

### 2.1 Luồng Xử lý Nghiệp vụ Sinh viên (Student Business Flow)
- **Tạo sinh viên:** Kiểm tra email đã tồn tại trong hệ thống chưa (`existsByEmail`). Nếu đã tồn tại, ném `IllegalStateException("Email taken")`. Ngược lại, thực hiện lưu vào CSDL trong một Transaction ACID (`@Transactional`).
- **Phân trang & Tìm kiếm:** Tích hợp `Pageable` để phân trang danh sách sinh viên. Hỗ trợ truy vấn JPQL tìm kiếm không phân biệt hoa thường (`findByEmailIgnoreCase`, JPQL search với `LIKE concat('%', :q, '%')`).

### 2.2 Mô hình Quan hệ Thực thể ORM (JPA Mappings)
Hệ thống thể hiện đầy đủ các mô hình quan hệ cơ sở dữ liệu quan hệ nâng cao:

#### A. Quan hệ 1:1 (One-to-One: `Author` ↔ `AuthorProfile`)
- **Owning Side (Bên sở hữu FK):** `AuthorProfile` chứa `@JoinColumn(name = "author_id")`.
- **Inverse Side:** `Author` sử dụng `mappedBy = "author"`, cấu hình `cascade = CascadeType.ALL` và `orphanRemoval = true`.
- **Fetch Strategy:** Cấu hình bắt buộc `fetch = FetchType.LAZY` để tránh truy vấn mặc định EAGER gây suy giảm hiệu năng.

#### B. Quan hệ 1:N & N:1 (One-to-Many / Many-to-One: `Publisher` ↔ `Book`)
- **Owning Side:** `Book` chứa `@ManyToOne(fetch = FetchType.LAZY)` và `@JoinColumn(name = "publisher_id")`.
- **Inverse Side:** `Publisher` chứa `@OneToMany(mappedBy = "publisher")`. Khuyến nghị dùng `Set<Book>` thay vì `List<Book>` để Hibernate tối ưu thao tác tập hợp.

#### C. Quan hệ N:N (Many-to-Many: `Book` ↔ `Author`)
- **Owning Side:** `Book` quản lý bảng trung gian `@JoinTable(name = "book_authors")` chứa `book_id` và `author_id`.
- **Inverse Side:** `Author` khai báo `@ManyToMany(mappedBy = "authors")`.
- **Best Practice:** Bắt buộc dùng `Set<Author>` nhằm tránh tình trạng Hibernate xóa và chèn lại toàn bộ danh sách khi thay đổi 1 phần tử.

---

## 3. Mẫu Thiết kế & Chuẩn hóa API (Design Patterns & API Standards)

### 3.1 DTO Pattern (Data Transfer Object) với Java 17 Records
1. **Khép kín Domain Model:** Không bao giờ lộ trực tiếp `@Entity` ra API bên ngoài. DTO giúp giấu các thông tin nhạy cảm hoặc logic sinh ID nội bộ.
2. **Sử dụng Java 17 Records (`StudentRequest`, `StudentResponse`):**
   - Đảm bảo tính bất biến (Immutable Data Carriers).
   - Giảm thiểu boilerplate code (tự động tạo `getters`, `equals`, `hashCode`, `toString`).
3. **Phân tách Validation Boundary:**
   - DTO gán các Validation Annotations (`@NotBlank`, `@Email`, `@PastOrPresent`).
   - Ràng buộc API tách biệt hoàn toàn với ràng buộc DB Schema.

### 3.2 Global Exception Handling (`@RestControllerAdvice`)
Tập trung hóa xử lý ngoại lệ toàn ứng dụng, loại bỏ các khối `try-catch` rải rác tại Controller:
- Catch `MethodArgumentNotValidException` để trích xuất các lỗi trường (`getFieldErrors()`).
- Trả về cấu trúc lỗi JSON đồng nhất và HTTP Status Code tương ứng (400 Bad Request, 404 Not Found, 500 Internal Error).

### 3.3 Transaction Management (`@Transactional`)
- Đảm bảo tính chất **ACID** cho toàn bộ thao tác ghi/sửa dữ liệu.
- Tự động Rollback dữ liệu khi phát sinh `RuntimeException`.

---

## 4. Chiến lược Tối ưu hóa Hiệu năng Data Access (Data Access Strategy)

### 4.1 Giải quyết triệt để sự cố N+1 Query Problem
- **Nguyên nhân:** Khi load 1 danh sách $N$ entity cha và truy cập thuộc tính con LAZY, Hibernate thực hiện 1 truy vấn ban đầu + $N$ truy vấn con.
- **Giải pháp:**
  1. **JPQL JOIN FETCH:** `SELECT s FROM Student s JOIN FETCH s.address`
  2. **Entity Graph (`@EntityGraph`):** Khai báo fetch plan tĩnh/động trên phương thức Repository.
  3. **DTO Projections:** Truy vấn trực tiếp các cột cần thiết vào DTO Record để bỏ qua overhead chuyển đổi Entity.

### 4.2 Hibernate & Persistence Best Practices
- **Default to LAZY:** Thiết lập `fetch = FetchType.LAZY` cho tất cả các quan hệ (`@OneToOne`, `@ManyToOne`, `@OneToMany`, `@ManyToMany`).
- **Tắt Open Session in View (OSIV):** Đặt `spring.jpa.open-in-view=false` để đóng Persistence Context ngay sau Service Layer, ngăn ngừa truy vấn ngầm ngoài luồng giao dịch.
- **Read-Only Transactions:** Sử dụng `@Transactional(readOnly = true)` cho các API chỉ đọc để tối ưu bộ nhớ và bỏ qua dirty-checking của Hibernate.

---

## 5. Kiến trúc Vận hành & Quan sát (Observability Architecture)

1. **Structured Metrics & Tracing:** Sử dụng **Micrometer Tracing** tích hợp sẵn trong Spring Boot 3.x để theo dõi vết request xuyên suốt các tầng.
2. **Containerization & Cloud Readiness:** Khả năng tương thích native image với GraalVM AOT (Ahead-Of-Time compilation) mang lại thời gian khởi động tính bằng mili-giây và giảm thiểu footprint bộ nhớ RAM.

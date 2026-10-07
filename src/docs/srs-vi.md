# Tài liệu Đặc tả Yêu cầu Phần mềm (Software Requirements Specification - SRS)
**Tên dự án:** Hệ thống Quản lý Sinh viên (Student Management System)  
**Nền tảng:** Spring Boot 3.x & Spring Framework 6.x  
**Ngôn ngữ tài liệu:** Tiếng Việt  
**Phiên bản:** 1.0  

---

## 1. Giới thiệu (Introduction)

### 1.1 Mục đích tài liệu
Tài liệu Đặc tả Yêu cầu Phần mềm (SRS) này mô tả chi tiết các yêu cầu chức năng, yêu cầu phi chức năng, kiến trúc phần mềm và các ràng buộc kỹ thuật của **Hệ thống Quản lý Sinh viên (Student Management System)**. Tài liệu phục vụ làm căn cứ cho việc phát triển, kiểm thử, nghiệm thu và bàn giao phần mềm.

### 1.2 Phạm vi hệ thống
Hệ thống cung cấp dịch vụ RESTful API backend cho phép thực hiện các thao tác CRUD (Tạo, Đọc, Cập nhật, Xóa), tìm kiếm, phân trang thông tin sinh viên, quản lý mối quan hệ dữ liệu nâng cao, xử lý lỗi tập trung và hỗ trợ đóng gói container hóa cho môi trường triển khai thực tế.

### 1.3 Định nghĩa & Từ viết tắt
- **CRUD:** Create, Read, Update, Delete (Thêm, Đọc, Sửa, Xóa).
- **DTO:** Data Transfer Object (Đối tượng chuyển giao dữ liệu).
- **JPA:** Java Persistence API / Jakarta Persistence.
- **IoC / DI:** Inversion of Control / Dependency Injection.
- **RFC 7807:** Chuẩn định dạng chi tiết lỗi HTTP REST API.
- **AOT:** Ahead-Of-Time compilation (Biên dịch trước thời gian chạy).

---

## 2. Mô tả Tổng quan (Overall Description)

### 2.1 Bối cảnh hệ thống
Hệ thống hoạt động như một dịch vụ Backend Microservice độc lập, giao tiếp với các ứng dụng Frontend (Web, Mobile, Postman) qua giao thức HTTP/JSON RESTful.

### 2.2 Phân loại Người dùng & Đặc điểm
- **Client / Web Application:** Tương tác với hệ thống để gửi yêu cầu quản lý danh sách sinh viên.
- **System Administrator:** Triển khai, vận hành, theo dõi sức khỏe hệ thống qua Actuator và Docker.

### 2.3 Môi trường Vận hành
- **Môi trường chạy:** Java 17 LTS trở lên (Virtual Threads / GraalVM Native Image support).
- **Cơ sở dữ liệu:** MySQL 8.0 / H2 Database.
- **Nền tảng Container:** Docker Engine & Docker Compose.

---

## 3. Yêu cầu Chức năng (Functional Requirements - FR)

### FR-1: Quản lý Thông tin Sinh viên (Student CRUD)
- **FR-1.1 (Tạo sinh viên):** Cho phép người dùng gửi thông tin sinh viên mới bao gồm Họ, Tên, Email, Ngày sinh.
  - *Ràng buộc:* Email phải là duy nhất (`unique = true`). Nếu email đã tồn tại, hệ thống phải từ chối và trả về lỗi.
- **FR-1.2 (Lấy danh sách sinh viên):** Hỗ trợ lấy danh sách sinh viên có phân trang (`page`, `size`) và sắp xếp.
- **FR-1.3 (Xem chi tiết sinh viên):** Trả về thông tin chi tiết của sinh viên dựa trên ID. Trả về `404 Not Found` nếu không tìm thấy.
- **FR-1.4 (Cập nhật sinh viên):** Cập nhật thông tin sinh viên theo ID.
- **FR-1.5 (Xóa sinh viên):** Xóa sinh viên khỏi cơ sở dữ liệu theo ID. Trả về `204 No Content` khi thành công.

### FR-2: Tìm kiếm & Lọc Dữ liệu (Search & Filtering)
- **FR-2.1:** Tìm kiếm sinh viên chính xác theo Email không phân biệt hoa thường (`findByEmailIgnoreCase`).
- **FR-2.2:** Tìm kiếm sinh viên theo từ khóa chứa trong Họ hoặc Tên (`search` query qua JPQL `LIKE`).

### FR-3: Xác thực Dữ liệu Đầu vào (Input Validation Boundary)
- **FR-3.1:** `firstName` và `lastName` không được để trống (`@NotBlank`).
- **FR-3.2:** `email` phải đúng định dạng chuẩn (`@Email`) và không được rỗng (`@NotBlank`).
- **FR-3.3:** `dob` (ngày sinh) phải là một ngày trong quá khứ hoặc hiện tại (`@PastOrPresent`).

### FR-4: Quản lý Mối quan hệ Thực thể Domain (Domain Relationship Management)
- **FR-4.1 (Quan hệ 1:1):** Liên kết `Author` và `AuthorProfile` (Cascading ALL, Orphan Removal).
- **FR-4.2 (Quan hệ 1:N / N:1):** Liên kết `Publisher` và `Book` với chiến lược Lazy Loading.
- **FR-4.3 (Quan hệ N:N):** Liên kết `Book` và `Author` thông qua bảng trung gian `book_authors` sử dụng `Set<T>`.

### FR-5: Xử lý Lỗi Tập trung (Centralized Error Management)
- **FR-5.1:** Hệ thống phải bắt tất cả ngoại lệ phát sinh tại tầng Controller thông qua `@RestControllerAdvice`.
- **FR-5.2:** Trả về cấu trúc lỗi chuẩn bao gồm `timestamp`, `status`, `error`, và danh sách `details`.

---

## 4. Yêu cầu Phi chức năng (Non-Functional Requirements - NFR)

### NFR-1: Hiệu năng & Tối ưu CSDL (Performance)
- **NFR-1.1:** Thời gian phản hồi cho các thao tác CRUD thông thường phải $< 200	ext{ ms}$.
- **NFR-1.2:** Triệt tiêu hoàn toàn sự cố **N+1 Query Problem** bằng cách áp dụng `JOIN FETCH` hoặc `@EntityGraph`.
- **NFR-1.3:** Tắt Open Session in View (`spring.jpa.open-in-view=false`) để tối ưu hóa quản lý kết nối CSDL.

### NFR-2: Bảo mật & Khép kín Dữ liệu (Security & Encapsulation)
- **NFR-2.1:** Không sử dụng trực tiếp JPA Entity làm dữ liệu trao đổi API. Bắt buộc chuyển đổi qua DTO Records (`StudentRequest`, `StudentResponse`).
- **NFR-2.2:** Quản lý mật khẩu CSDL và khóa bảo mật qua biến môi trường (Environment Variables).

### NFR-3: Khả năng Bảo trì & Mở rộng (Maintainability)
- **NFR-3.1:** Áp dụng Constructor Injection thay cho Field Injection để tăng tính bất biến và dễ viết Unit Test.
- **NFR-3.2:** Mã nguồn tuân thủ kiến trúc phân tầng rõ ràng (Controller -> Service -> Repository -> Entity).

### NFR-4: Đóng gói & Triển khai (Portability & Deployment)
- **NFR-4.1:** Phần mềm phải được đóng gói thành Docker Container hoạt động độc lập.
- **NFR-4.2:** Tích hợp tính năng Health check & Observability sẵn sàng cho môi trường Kubernetes / Cloud Native.

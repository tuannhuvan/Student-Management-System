# Kế hoạch Bàn giao & Triển khai Hệ thống (Delivery & Deployment Guide)
**Dự án:** Student Management System (Spring Boot 3.x CRUD)  
**Phiên bản:** 1.0.0  
**Tài liệu tham chiếu:** Spring Framework 6.x & Spring Boot 3.x CRUD Application Guide  

---

## 1. Tổng quan Dự án & Phạm vi Bàn giao (Scope & Deliverables)

Hệ thống **Student Management System** là một ứng dụng RESTful API hiện đại được xây dựng dựa trên hệ sinh thái **Spring Boot 3.x** và **Spring Framework 6.x**, sử dụng **Java 17+ baseline** và chuẩn **Jakarta EE 9+** (chuyển đổi hoàn toàn từ package `javax.*` sang `jakarta.*`).

### Các thành phần phần mềm bàn giao:
1. **Mã nguồn ứng dụng (Source Code):**
   - Mã nguồn Java theo cấu trúc phân tầng (Controller, Service, Repository, Entity, DTO, Global Exception Handling).
   - Tệp cấu hình ứng dụng: `pom.xml`, `application.properties`, `application-dev.properties`, `application-prod.properties`.
2. **Thành phần đóng gói & Containerization:**
   - `Dockerfile`: Tệp đóng gói ứng dụng thành Docker Container nhẹ trên nền `eclipse-temurin:17-jre-alpine`.
   - `docker-compose.yml`: Tệp điều phối các dịch vụ ứng dụng (Spring Boot App) và cơ sở dữ liệu (MySQL Database).
3. **Tài liệu API & Kiểm thử:**
   - Tích hợp **OpenAPI 3 / Swagger UI** (`/swagger-ui.html`) qua thư viện `springdoc-openapi`.
   - Bộ lệnh kiểm thử tự động cURL và bộ Collection Postman/Insomnia.
4. **Cơ sở dữ liệu:**
   - DDL/DML Script khởi tạo cơ sở dữ liệu MySQL (`student_db`) và tích hợp Hibernate Auto-DDL (`spring.jpa.hibernate.ddl-auto=update`).

---

## 2. Yêu cầu Môi trường (System & Environment Requirements)

| Tên thành phần | Yêu cầu tối thiểu | Khuyến nghị |
| :--- | :--- | :--- |
| **Java Runtime (JDK)** | OpenJDK / Eclipse Temurin 17 | JDK 17+ LTS hoặc JDK 21 |
| **Build Tool** | Apache Maven 3.8+ | Maven Wrapper (`./mvnw`) đi kèm project |
| **Database** | H2 Database (Dev/Test) | MySQL 8.0+ (Production) |
| **Container Engine** | Docker Engine 20.10+ | Docker Desktop / Podman |
| **Orchestration** | Docker Compose v2.0+ | Docker Compose v2.x |

---

## 3. Quy trình Đóng gói & Xây dựng (Build & Packaging Pipeline)

### 3.1 Xây dựng ứng dụng với Maven
Sử dụng Maven Wrapper để đóng gói ứng dụng thành file JAR khả thi (Executable JAR):
```bash
# Xóa bộ nhớ đệm và đóng gói JAR
./mvnw clean package -DskipTests
```
File JAR tạo ra nằm tại thư mục: `target/student-management-service-1.0.0.jar`.

### 3.2 Đóng gói Docker Image (`Dockerfile`)
Sử dụng hình ảnh cơ sở siêu nhẹ Alpine Linux giúp tối ưu dung lượng container và tăng tốc độ khởi động:

```dockerfile
# Use a lightweight JRE base image
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### 3.3 Điều phối ứng dụng với Docker Compose (`docker-compose.yml`)
Khởi chạy đồng thời Spring Boot Service và MySQL Database với biến môi trường Production:

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/studentdb?useSSL=false&allowPublicKeyRetrieval=true
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=secret
    depends_on:
      - db

  db:
    image: mysql:8.0
    environment:
      - MYSQL_DATABASE=studentdb
      - MYSQL_ROOT_PASSWORD=secret
    ports:
      - "3306:3306"
```

---

## 4. Cấu hình Môi trường (Environment Configuration)

Hệ thống hỗ trợ cơ chế Externalized Configuration thông qua **Spring Profiles**:

### 4.1 Môi trường Phát triển (`application-dev.properties`)
```properties
# Server Port
server.port=8080

# Database Configuration (H2 / MySQL Dev)
spring.datasource.url=jdbc:mysql://localhost:3306/student_db
spring.datasource.username=root
spring.datasource.password=secret

# JPA / Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

### 4.2 Môi trường Vận hành (`application-prod.properties`)
```properties
server.port=8080
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.open-in-view=false
```

---

## 5. Hướng dẫn Kiểm thử & Vận hành (Operational & Testing Guide)

### 5.1 Lệnh kiểm thử API qua cURL
1. **Tạo mới Sinh viên (POST):**
```bash
curl -X POST http://localhost:8080/api/students   -H "Content-Type: application/json"   -d '{
    "firstName": "Ana",
    "lastName": "Nguyen",
    "email": "ana@example.com",
    "dob": "2000-01-01"
  }'
```
*Response Expected:* `HTTP 201 Created`

2. **Lấy danh sách Sinh viên có phân trang (GET):**
```bash
curl "http://localhost:8080/api/students?page=0&size=5"
```
*Response Expected:* `HTTP 200 OK` chứa đối tượng `Page<StudentResponse>`.

3. **Xóa Sinh viên theo ID (DELETE):**
```bash
curl -X DELETE http://localhost:8080/api/students/1
```

### 5.2 Kiểm tra Sức khỏe Hệ thống (Observability & Actuator)
Tích hợp **Spring Boot Actuator** & **Micrometer** cung cấp metric và tracing:
- Endpoint kiểm tra trạng thái: `GET /actuator/health`
- Swagger UI Documentation: `http://localhost:8080/swagger-ui.html`

---

## 6. Danh mục API Specification & Standard Response

### Danh sách API Endpoints:
| HTTP Method | URI Path | Description | Expected Status |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/students` | Tạo mới sinh viên | `201 Created` |
| `GET` | `/api/students` | Lấy danh sách (Phân trang `Pageable`) | `200 OK` |
| `GET` | `/api/students/{id}` | Chi tiết sinh viên theo ID | `200 OK` / `404 Not Found` |
| `PUT` | `/api/students/{id}` | Cập nhật thông tin sinh viên | `200 OK` / `400 Bad Request` |
| `DELETE` | `/api/students/{id}` | Xóa sinh viên | `204 No Content` / `404` |

### Định dạng Lỗi Chuẩn (RFC 7807 Problem Details):
Khi xảy ra lỗi Validation hoặc Business Exception, hệ thống phản hồi định dạng JSON đồng nhất:
```json
{
  "timestamp": 1770000000000,
  "status": 400,
  "error": "Validation Failed",
  "details": [
    "email: must be a well-formed email address",
    "firstName: must not be blank"
  ]
}
```

# CodeGraph & Architectural Dependency Rules

Tài liệu này định nghĩa sơ đồ phụ thuộc mã nguồn (CodeGraph) và các quy tắc ranh giới kiến trúc áp dụng cho Dự án Spring Boot 3 / Java 17 Student Management System.

---

## 1. Sơ đồ Phụ thuộc Thành phần (Component Dependency Graph)

```
[ HTTP Request (Client / Frontend) ]
                │
                ▼
[ REST Controller Layer (@RestController) ] ──(Uses DTO Records)──► [ DTO Layer (Records / Validation) ]
                │
                ▼
[ Service Layer (@Service) ] ──(Business Logic / Transactions)
                │
                ▼
[ Repository Layer (@Repository) ] ──(Spring Data JPA / JPQL)
                │
                ▼
[ Domain Entity Layer (@Entity) ] ──(ORM Entities)
                │
                ▼
[ Database (MySQL 8.0 / PostgreSQL) ]
```

---

## 2. Quy tắc Chiều Phụ thuộc (Dependency Direction Rules)

1. **Chiều một hướng (Unidirectional Flow):**
   - `Controller` -> `Service` -> `Repository` -> `Database`.
   - **Tuyệt đối CẤM:** Call ngược từ `Repository` -> `Service`, hoặc từ `Service` -> `Controller`.
   - **Tuyệt đối CẤM:** `Controller` gọi trực tiếp `Repository` bỏ qua `Service`.

2. **Ranh giới DTO (DTO Boundary Enforcement):**
   - Layer `Controller` chỉ làm việc với `DTO` (Request DTO & Response DTO) thông qua Java 17 Records (`StudentRequest`, `StudentResponse`).
   - Domain Entities (`@Entity`) không bao giờ được trả trực tiếp ra ngoài REST API để tránh rò rỉ dữ liệu (data leakage) và lỗi `LazyInitializationException` khi serialization.
   - Việc chuyển đổi giữa `Entity` và `DTO` được thực hiện hoàn toàn tại tầng `Service` (hoặc thông qua Mapper dedicated).

3. **Giao tiếp liên Module/Service (Inter-Service Boundary):**
   - Nếu dự án phát triển theo hướng Modulith hoặc Microservices, các module không gọi trực tiếp Repository của nhau. Việc giao tiếp giữa các domain bắt buộc đi qua Service interface public hoặc Event Bus.

---

## 3. Ma trận Cho phép Phụ thuộc (Dependency Matrix)

| Source Layer | Allowed Target Layers | Prohibited Target Layers |
| :--- | :--- | :--- |
| **Controller** | Service, DTO, Exception Handler | Repository, Entity, Database |
| **Service** | Repository, Entity, DTO, Exception Domain | Controller, External REST Clients trực tiếp tại Controller |
| **Repository** | Entity, Spring Data JPA Interfaces | Controller, Service, DTO |
| **Entity** | Java Standard Library, Jakarta Persistence | Controller, Service, Repository, DTO |
| **DTO (Record)** | Java Standard Library, Jakarta Validation | Controller, Service, Repository, Entity |

---

## 4. Kiểm soát Kiến trúc Tự động (ArchUnit Rules)

Dự án áp dụng thư viện **ArchUnit** trong Unit Test suite để đảm bảo không vi phạm CodeGraph:

```java
@AnalyzeClasses(packages = "com.example.student")
public class ArchitectureRulesTest {

    @ArchTest
    public static final ArchRule controllers_should_only_depend_on_services =
        classes().that().resideInAPackage("..controller..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage("..service..", "..dto..", "..exception..", "java..", "org.springframework..");

    @ArchTest
    public static final ArchRule entities_should_not_depend_on_dto =
        noClasses().that().resideInAPackage("..entity..")
            .should().dependOnClassesThat().resideInAPackage("..dto..");
}
```

# Go-Task Execution & Change Specification (Taskfile Protocol)

Đặc tả công cụ điều hành tác vụ `Taskfile.yml` (Go-Task) và quy trình quản lý thay đổi (Change Protocol) cho dự án backend.

---

## 1. Cấu hình Go-Task (`Taskfile.yml`)

Dự án sử dụng `task` (Go-Task) thay cho `make` để đảm bảo tính đồng bộ trên cả Linux, macOS và Windows.

```yaml
version: '3'

vars:
  APP_NAME: student-management-service
  DOCKER_IMAGE: student-management:latest
  MAVEN_CMD: ./mvnw

tasks:
  default:
    desc: "Hiển thị danh sách các lệnh khả dụng"
    cmds:
      - task --list

  build:
    desc: "Biên dịch dự án và đóng gói JAR"
    cmds:
      - "{{.MAVEN_CMD}} clean package -DskipTests"

  test:
    desc: "Chạy toàn bộ Unit và Integration Tests"
    cmds:
      - "{{.MAVEN_CMD}} clean test integration-test"

  dev:
    desc: "Khởi chạy ứng dụng môi trường Local (Spring Profile: dev)"
    cmds:
      - "{{.MAVEN_CMD}} spring-boot:run -Dspring-boot.run.profiles=dev"

  docker:up:
    desc: "Khởi chạy môi trường Docker Compose (Database + App)"
    cmds:
      - docker-compose up -d --build

  docker:down:
    desc: "Dừng và xóa toàn bộ Docker containers"
    cmds:
      - docker-compose down -v

  db:migrate:
    desc: "Chạy Migration CSDL bằng Flyway"
    cmds:
      - "{{.MAVEN_CMD}} flyway:migrate"
```

---

## 2. Quy trình Quản lý Thay đổi (Change Specification Protocol)

Mọi thay đổi liên quan đến Logic, CSDL hoặc API Contract phải tuân thủ các bước:

1. **Change Request (CR) Proposal:**
   - Tạo issue trên Git tracker với nhãn `change-spec`.
   - Mô tả phạm vi ảnh hưởng (API, Database, Performance).

2. **Database Migration Rules:**
   - Mọi thay đổi schema CSDL phải viết script SQL migration đặt tại `src/main/resources/db/migration/`.
   - Quy tắc đặt tên Flyway: `V<Version>__<Description>.sql` (Ví dụ: `V2__add_gpa_column_to_students.sql`).
   - **CẤM:** Sửa trực tiếp các file SQL migration đã merge vào nhánh `main`.

3. **API Backward Compatibility Checklist:**
   - Không xóa field hiện tại trong Response JSON.
   - Thêm field mới phải để `nullable` hoặc có giá trị mặc định.
   - Nếu thay đổi Breaking Change: Phải nâng cấp phiên bản API (Ví dụ: `/api/v1/...` -> `/api/v2/...`).

4. **Automated Pull Request Gate:**
   - PR chỉ được merge khi vượt qua `task test` và coverage đạt tối thiểu 80%.

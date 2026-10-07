# DevOps, Docker & CI/CD Guidelines

Quy chuẩn Đóng gói, Triển khai và Giám sát Ứng dụng Backend Spring Boot 3.

---

## 1. Docker Multi-Stage Build Protocol

Tệp `Dockerfile` tối ưu kích thước image và nâng cao tính bảo mật bằng cách phân tách đoạn build và đoạn runtime:

```dockerfile
# Build Stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

---

## 2. Quy trình CI/CD Pipeline (GitHub Actions)

1. **Code Analysis & Testing Gate:**
   - Run Checkstyle & SonarQube static code analysis.
   - Run Unit tests & Integration tests.
2. **Container Registry Push:**
   - Build Docker Image và gán tag theo Git Commit SHA.
   - Push image lên Docker Hub / Amazon ECR.
3. **Deployment Gate:**
   - Auto-deploy đến môi trường Staging.
   - Manual Approval để deploy Production.

---

## 3. Observability & Monitoring

- **Spring Boot Actuator Endpoints:** Expose `/actuator/health` và `/actuator/prometheus`.
- **Metrics Collection:** Tích hợp Prometheus + Grafana dashboard theo dõi JVM Memory, CPU usage, Connection Pool, và API latency.

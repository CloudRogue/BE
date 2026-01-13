# =========================
# 1) Build stage
# =========================
FROM gradle:8.7-jdk21 AS builder
WORKDIR /workspace

COPY . .
RUN chmod +x gradlew

# auth 모듈만 bootJar 생성
RUN ./gradlew :apps:auth:bootJar --no-daemon

# =========================
# 2) Runtime stage
# =========================
FROM amazoncorretto:21-al2023-headless
WORKDIR /app

COPY --from=builder /workspace/apps/auth/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

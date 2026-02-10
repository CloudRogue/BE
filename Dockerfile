# =========================
# 1) Build stage
# =========================
FROM gradle:8.7-jdk21 AS builder
WORKDIR /workspace

COPY . .
RUN chmod +x gradlew

ARG APP_NAME
RUN ./gradlew :apps:${APP_NAME}:bootJar --no-daemon

# =========================
# 2) Runtime stage
# =========================
FROM amazoncorretto:21-al2023-headless
WORKDIR /app

ARG APP_NAME
COPY --from=builder /workspace/apps/${APP_NAME}/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
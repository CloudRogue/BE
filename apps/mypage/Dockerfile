# =========================
# 1) Build stage
# =========================
FROM gradle:8.7-jdk21 AS builder
WORKDIR /app

# 루트 컨텍스트에서 전체 파일 복사
COPY . .
RUN chmod +x gradlew

# mypage 모듈만 bootJar 생성
RUN ./gradlew :apps:mypage:bootJar --no-daemon

# =========================
# 2) Runtime stage
# =========================
FROM amazoncorretto:21-al2023-headless
WORKDIR /app

# 빌드 스테이지의 /app 경로에서 생성된 jar를 복사
COPY --from=builder /app/apps/mypage/build/libs/*.jar app.jar

EXPOSE 8080 
ENTRYPOINT ["java","-jar","app.jar"]

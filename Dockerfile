# 1. Java 실행 환경(JDK) 가져오기
FROM amazoncorretto:21-al2023-headless
# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 경로 (본인 프로젝트의 빌드 경로 확인!)
# Gradle은 build/libs/*.jar, Maven은 target/*.jar
ARG JAR_FILE=build/libs/auth-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
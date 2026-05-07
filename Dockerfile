# 1. 빌드된 jar 파일이 있는 위치 설정 (프로젝트 구조에 따라 다를 수 있음)
# 보통 Gradle 빌드 후 build/libs/*.jar 경로에 생성됩니다.
FROM eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. JAR 파일을 컨테이너 내부로 복사
# buildArgs를 사용하거나 파일명을 직접 지정하세요.
# 예: 프로젝트명이 coffee-order라면 coffee-order-0.0.1-SNAPSHOT.jar
COPY build/libs/*.jar app.jar

# 4. 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
# 베이스 이미지 설정
FROM openjdk:21

# 인자 설정 - JAR 파일의 경로를 유연하게 설정
ARG JAR_FILE=build/libs/*.jar

# 로컬에서 빌드된 JAR 파일을 컨테이너 안으로 복사
COPY ${JAR_FILE} app.jar

# 컨테이너 실행 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]

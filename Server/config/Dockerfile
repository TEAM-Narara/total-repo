# 베이스 이미지 설정
FROM openjdk:21

# 인자 설정 - JAR 파일의 경로를 유연하게 설정
ARG JAR_FILE=build/libs/*.jar

# ../config-s107 폴더를 컨테이너 내부로 복사
COPY ../config-s107 /config-s107
COPY ../SuperBoard/s107-secret-value /s107-secret-value

# 복사가 잘 되었는지 로그 출력
RUN echo "Checking /config-s107 directory contents:" && ls -la /config-s107

# 로컬에서 빌드된 JAR 파일을 컨테이너 안으로 복사
COPY ${JAR_FILE} app.jar

# 복사된 JAR 파일 확인
RUN echo "Checking app.jar file:" && ls -la /app.jar

# 컨테이너 실행 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]

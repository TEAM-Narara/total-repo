# 1. Gitlab 소스 클론 이후 빌드 및 배포할 수 있도록 정리한 문서

## 1. 사용한 JVM, 웹서버, WAS 제품 등의 종류와 설정 값, 버전  
- **JVM**: Java 21  
- **WAS 제품**: Spring Boot 내장 WAS  
- **IDE 버전**:  
  - IntelliJ IDEA 2024.2.1  
  - Android Studio 2024.2.1  

## 2. 빌드 시 사용되는 환경 변수  

### 2.1 Server 설정  
- **관련 파일**:  
  - [build.gradle](https://lab.ssafy.com/s11-final/S11P31S107/-/blob/BE/develop/Server/SuperBoard/build.gradle?ref_type=heads)  
  - [Server/SuperBoard 디렉토리 내 .gradle 파일](https://lab.ssafy.com/s11-final/S11P31S107/-/blob/BE/develop/Server/SuperBoard/?ref_type=heads)  

### 2.2 Android 설정  
- **관련 파일**:  
  - [프로젝트 빌드 파일](https://lab.ssafy.com/s11-final/S11P31S107/-/blob/main/Android/build.gradle.kts?ref_type=heads)  
  - [어플리케이션 빌드 파일](https://lab.ssafy.com/s11-final/S11P31S107/-/blob/main/Android/app/build.gradle.kts?ref_type=heads)  
  - [버전 관리 파일](https://lab.ssafy.com/s11-final/S11P31S107/-/blob/main/Android/gradle/libs.versions.toml?ref_type=heads)  


## 3. 배포 시 특이사항  

1. **NGINX 설정**  
   - 스프링 백엔드 서버는 NGINX의 **80 포트**를 중심으로 설정되어 있습니다.  

2. **Android 알람 설정**  
   - `google-service.json` 파일이 보안을 위해 제외되었습니다.  
   - **앱 디렉토리**에 해당 파일을 추가해야 알림 기능을 정상적으로 사용할 수 있습니다.  

3. **서버 연동 시 Secret Key**  
   - 보안을 위해 일부 Secret Key 값이 제외되었습니다.  
   - 올바른 Secret Key 값을 주석 부분에 작성해야 서버와의 연동이 가능합니다.  


## 4. DB 접속 정보 및 주요 계정 정의 파일  

### 4.1 사용된 데이터베이스  
- **PostgreSQL**  
- **MongoDB**  
- **Redis**  
- **Kafka**  

### 4.2 DB 관련 파일 목록  
- 프로젝트와 ERD에 활용되는 주요 계정 및 프로퍼티 정보는 아래 파일들에 정의되어 있습니다:  
  - **PostgreSQL 설정**: `application-postgres.properties`  
  - **MongoDB 설정**: `application-mongo.properties`  
  - **Redis 설정**: `application-redis.properties`  
  - **Kafka 설정**: `application-kafka.properties`  


## 참고  
- 필요한 Secret Key 및 Google Service 파일은 프로젝트 담당자로부터 별도로 요청해야 합니다.  


# 2. 프로젝트에서 사용하는 외부 서비스 정보를 정리한 문서  
   - Github 소셜 인증
   - Naver 소셜 인증


# 3. DB 덤프 파일 최신본  
   [dump.zip](https://lab.ssafy.com/s11-final/S11P31S107/-/blob/master/exec/dump.zip) : 스키마 전체 dml + ddl 압축 파일

# 4. 시연 시나리오  
   [시연시나리오](https://lab.ssafy.com/s11-final/S11P31S107/-/blob/docs/exec/시연시나리오.md) 파일에 따로 작성

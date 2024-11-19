1. Gitlab 소스 클론 이후 빌드 및 배포할 수 있도록 정리한 문서  
   1) 사용한 JVM, 웹서버, WAS 제품 등의 종류와 설정 값, 버전(IDE버전 포함) 기재
   - 사용한 jvm : java 21
   - WAS 제품 : spring boot 내장 was
   - IDE 버전 : Intellij IDEA 2024.2.1, Android Studio 2024.2.1

   2) 빌드 시 사용되는 환경 변수 등의 내용 상세 기재 (링크 참조)
   - Server 설정 : [build.gradle](https://lab.ssafy.com/s11-final/S11P31S107/-/blob/BE/develop/Server/SuperBoard/build.gradle?ref_type=heads) 
   및 [Server/SuperBoard 아래의 .gradle](https://lab.ssafy.com/s11-final/S11P31S107/-/blob/BE/develop/Server/SuperBoard/?ref_type=heads)에 기재
   - Android 설정 : 

   3) 배포 시 특이사항 기재  
    - 스프링 백서버가 nginx 80포트를 중심으로 설정되어 있습니다.

   4) DB 접속 정보 등 프로젝트(ERD)에 활용되는 주요 계정 및 프로퍼티가 정의된 파일 목록
    - Postgresql
    - MongoDB
    - Redis
    - Kafka

2. 프로젝트에서 사용하는 외부 서비스 정보를 정리한 문서  
   : 소셜 인증, 포톤 클라우드, 코드 컴파일 등에 활용 된 ‘외부 서비스’가입 및 활용에 필요한 정보
   - Github 소셜 인증
   - Naver 소셜 인증



3. DB 덤프 파일 최신본  
    - [dump.zip](https://lab.ssafy.com/s11-final/S11P31S107/-/blob/master/exec/dump.zip) : 스키마 전체 dml + ddl 압축 파일

4. 시연 시나리오  
   [시연시나리오](https://lab.ssafy.com/s11-final/S11P31S107/-/blob/docs/exec/시연시나리오.md) 파일에 따로 작성
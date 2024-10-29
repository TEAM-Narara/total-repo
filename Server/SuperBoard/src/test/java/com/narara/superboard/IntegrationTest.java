package com.narara.superboard;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Fail.fail;

/**
 * @Nested
 * 프로젝트가 점점 커져갈수록 테스트 코드도 커져간다.
 * 수많은 테스트 중 특정한 테스트의 수행 결과들을 찾기가 어렵다.
 * 이때, Nested 애노테이션을 이용하여 테스트를 다음과 같이 계층형으로 구성할 수 있다.
 * 같은 관심사의 테스트를 모아둘 수 있기 때문에 내가 원하는 테스트를 열어서 수행 결과들을 볼 수 있어 테스트 가독성이 향상되어 보기 한층 더 편했다.
 * @ActiveProfiles("test")
 * 역할: 테스트 환경에서 사용할 스프링 프로파일을 지정합니다.
 * 설명: test 프로파일을 활성화하여 테스트 실행 시 특정 설정 파일이나 빈 설정을 사용하도록 합니다.
 * 이를 통해 개발, 테스트, 프로덕션 등 환경별로 다른 설정을 쉽게 적용할 수 있습니다.
 * Spring이 application-test.yml 파일을 로드하고, 테스트 환경에 맞는 설정을 적용
 */
@Nested
@Transactional
@ActiveProfiles("test")
@SpringBootTest(classes = SuperBoardApplication.class)  // 메인 애플리케이션 클래스 지정
// 테스트 환경을 설정할 때 DB 연결을 분리하고 싶을 때 상속을 통해서 적용하세요.
public class IntegrationTest {
    @BeforeAll
    public static void checkTestProfile() {
//        System.out.println("application-test.yml 파일여부 확인(test_db 사용 여부)");
        if (!Files.exists(Paths.get("src/test/resources/application-test.yml"))) {
            fail("application-test.yml 파일이 존재하지 않습니다.");
        }
    }
}

package com.narara.integration;

import com.mongodb.client.MongoCollection;
import com.narara.superboard.SuperBoardApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.bson.Document;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
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
@DisplayName("통합 테스트 ")
// 테스트 환경을 설정할 때 DB 연결을 분리하고 싶을 때 상속을 통해서 적용하세요.
public class IntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate; // PostgreSQL 연결 확인

    @Autowired
    private RedisConnectionFactory redisConnectionFactory; // Redis 연결 확인

    @Autowired
    private MongoTemplate mongoTemplate; // MongoDB 연결 확인

    @BeforeAll
    public static void checkTestProfile() {
//        System.out.println("application-test.yml 파일여부 확인(test_db 사용 여부)");
        if (!Files.exists(Paths.get("src/test/resources/application-test.yml"))) {
            fail("application-test.yml 파일이 존재하지 않습니다.");
        }
    }

    @Test
    @DisplayName("테스트 환경에 맞는 설정을 적용하기")
    void contextLoads() {
    }

    @Test
    @DisplayName("PostgreSQL 연결 확인")
    void checkPostgresConnection() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            assertThat(result).isEqualTo(1);
            System.out.println("PostgreSQL 연결 성공");
        } catch (Exception e) {
            fail("PostgreSQL 연결 실패: " + e.getMessage());
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("Redis 연결 확인")
    void checkRedisConnection() {
        try (var connection = redisConnectionFactory.getConnection()) {
            Assert.notNull(connection, "Redis 연결 실패");
            System.out.println("Redis 연결 성공");
        } catch (Exception e) {
            fail("Redis 연결 실패: " + e.getMessage());
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)  // 이 테스트에서는 트랜잭션 비활성화
    @DisplayName("MongoDB 연결 확인")
    void checkMongoConnection() {
        try {
            // 간단한 작업을 통해 MongoDB 연결 확인
            mongoTemplate.getDb().getName();  // 연결 실패 시 예외가 발생

            // 선택 사항: 간단한 쓰기 작업 수행
            MongoCollection<Document> collection = mongoTemplate.getDb()
                    .getCollection("test_collection");

            // 테스트용 문서 삽입
            Document testDoc = new Document("test", "connection");
            collection.insertOne(testDoc);

            // 문서가 삽입되었는지 확인
            Document found = collection.find(new Document("test", "connection")).first();
            assertThat(found).isNotNull();

            // 정리
            collection.drop();

            System.out.println("MongoDB 연결 성공");
        } catch (Exception e) {
            fail("MongoDB 연결 실패: " + e.getMessage());
        }
    }

}

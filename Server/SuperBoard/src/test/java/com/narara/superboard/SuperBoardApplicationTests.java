package com.narara.superboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

import com.mongodb.client.MongoCollection;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Disabled
@DisplayName("어플리케이션 빌드 테스트")
@Nested
@Transactional
@ActiveProfiles("test")
@SpringBootTest()
class SuperBoardApplicationTests {

    @Test
    @DisplayName("실제 환경에 맞는 설정인지 확인 테스트")
    void contextLoads() {
    }


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
    @Disabled
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
    @Disabled
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
    @Disabled
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

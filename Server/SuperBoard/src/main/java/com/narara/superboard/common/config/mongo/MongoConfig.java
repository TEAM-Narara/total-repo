//package com.narara.superboard.common.config.mongo;
//
//import com.mongodb.ConnectionString;
//import com.mongodb.MongoClientSettings;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.MongoDatabaseFactory;
//import org.springframework.data.mongodb.MongoTransactionManager;
//import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
///**
// * MongoDB의 트랜잭션 기능은 Replica Set 환경에서만 작동합니다.
// * 만약 MongoDB가 Standalone 모드에서 작동 중이라면 트랜잭션이 적용되지 않고,
// * 예외가 발생하더라도 cardHistoryRepository.save(cardHistory);에서 저장된 데이터는 삭제되지 않습니다.
// *
// * 따라서 MongoDB가 Replica Set으로 설정되어 있는지 확인하세요.
// */
///**
// *
// * 2024년 11월 기준으로도 MongoDB에서 트랜잭션을 사용하려면 레플리카 셋(Replica Set) 구성이 필요합니다.
// * MongoDB는 버전 4.0부터 트랜잭션을 지원하기 시작했으며, 트랜잭션 기능은 레플리카 셋 환경에서만 동작합니다.
// * 이는 트랜잭션이 논리적 세션의 개념으로 만들어졌기 때문에, 레플리카 셋 환경에서만 가능한 oplog와 같은 기술이 필요하기 때문입니다.
// *
// * mongoDB는 트랜잭션은 선택이기때문에 Spring Boot에서 자동으로 올려주지 않기때문에
// * mongoDB의 트랜잭션을 관리하는mongoTransactionManager를 직접 스프링 컨테이너에 올려 주어야 합니다.
// */
//@Configuration // Spring 설정 클래스를 정의하는 어노테이션
//@EnableTransactionManagement // 트랜잭션 관리를 활성화하는 어노테이션
//public class MongoConfig extends
//        AbstractMongoClientConfiguration { // MongoDB 설정을 위해 AbstractMongoClientConfiguration을 상속받는 클래스
//
//    @Value("${spring.data.mongodb.uri}") // application.properties에서 MongoDB URI를 가져오는 어노테이션
//    private String connectionString; // MongoDB 연결 문자열을 저장하는 변수
//
//    @Value("${spring.data.mongodb.database}") // application.properties에서 MongoDB 데이터베이스 이름을 가져오는 어노테이션
//    private String databaseName; // MongoDB 데이터베이스 이름을 저장하는 변수
//
//    @Bean // Spring 컨텍스트에 빈으로 등록하는 어노테이션
//    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) { // MongoDB 트랜잭션 매니저를 생성하는 메서드
//        return new MongoTransactionManager(dbFactory); // dbFactory를 이용해 트랜잭션 매니저를 생성하고 반환
//    }
//
//    @Bean // Spring 컨텍스트에 빈으로 등록하는 어노테이션
//    public MongoTemplate mongoTemplate() { // MongoDB와 상호작용하는 MongoTemplate을 생성하는 메서드
//        return new MongoTemplate(mongoClient(), databaseName); // mongoClient와 databaseName을 사용하여 MongoTemplate 인스턴스를 반환
//    }
//
//    @Override
//    public MongoClient mongoClient() { // MongoDB 클라이언트를 생성하는 메서드
//        // 재시도 로직을 하지 않는 로직
////        String modifiedConnectionString = connectionString + (connectionString.contains("?") ? "&" : "?") + "retryWrites=false";
////        ConnectionString connectionString = new ConnectionString(modifiedConnectionString);
//
//        // 기존 로직
//        ConnectionString connectionString = new ConnectionString(
//                this.connectionString); // 저장된 연결 문자열로 ConnectionString 인스턴스를 생성
//
//        MongoClientSettings mongoClientSettings = MongoClientSettings.builder() // MongoDB 클라이언트 설정을 빌더로 구성
//                .applyConnectionString(connectionString) // 설정에 연결 문자열 적용
//                .build(); // 설정 빌더 완료 후 생성
//
//        return MongoClients.create(mongoClientSettings); // MongoDB 클라이언트를 생성하고 반환
//    }
//
//
//    @Override
//    protected String getDatabaseName() { // MongoDB 데이터베이스 이름을 반환하는 메서드
//        return databaseName; // 설정된 databaseName을 반환
//    }
//}

// 테스트
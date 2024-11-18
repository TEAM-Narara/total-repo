package com.narara.eureka;

import com.netflix.discovery.DiscoveryClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class EurekaApplicationTests {

    // Eureka 관련 빈을 목(Mock)으로 대체
    @MockBean
    private DiscoveryClient discoveryClient;


    @Test
    void contextLoads() {
    }

}

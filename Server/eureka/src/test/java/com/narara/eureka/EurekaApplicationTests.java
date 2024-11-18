package com.narara.eureka;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootTest
class EurekaApplicationTests {

    @Autowired
    private ApplicationContext context; // ApplicationContext 주입

    @Test
    void contextLoads() {
        Environment environment = context.getEnvironment(); // Environment 가져오기
        System.out.println("Environment variables: " + System.getenv());
        System.out.println("Active profiles: " + Arrays.toString(environment.getActiveProfiles()));
    }


}

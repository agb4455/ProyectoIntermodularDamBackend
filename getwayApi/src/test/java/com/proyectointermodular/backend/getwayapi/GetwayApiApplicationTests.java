package com.proyectointermodular.backend.getwayapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
                 properties = "spring.cloud.config.enabled=false")
class GetwayApiApplicationTests {

    @Test
    void contextLoads() {
    }

}

package com.tfm.db_back;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    @LocalServerPort
    protected int port;

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    static final MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    static {
        try {
            postgres.start();
            mongo.start();
            
            // Inyectar variables de entorno directamente en el sistema para que coincidan 
            // con los placeholders de application.properties (main)
            System.setProperty("POSTGRES_URL", postgres.getJdbcUrl());
            System.setProperty("MONGODB_URL", mongo.getReplicaSetUrl());
            System.setProperty("DB_HANDSHAKE_SECRET", "test-secret-mÃ­nimo-32-chars-ok!!");
        } catch (Exception e) {
            System.err.println("FAILED TO START TESTCONTAINERS: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Datasource properties
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        
        // Mongo properties
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);

        // Placeholders used in application.properties (main)
        registry.add("POSTGRES_URL", postgres::getJdbcUrl);
        registry.add("MONGODB_URL", mongo::getReplicaSetUrl);
        
        // Remove redundant spring.flyway.url/user/password to use the default DataSource
    }
}

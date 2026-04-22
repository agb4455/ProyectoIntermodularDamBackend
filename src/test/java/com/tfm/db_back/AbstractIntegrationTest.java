package com.tfm.db_back;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    @LocalServerPort
    protected int port;

    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @ServiceConnection
    static final MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    static {
        postgres.start();
        mongo.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // El secreto de handshake es necesario para que los tests pasen el filtro de seguridad
        registry.add("DB_HANDSHAKE_SECRET", () -> "test-secret-minimo-32-chars-ok-fixed!!");
        
        // Mapeo explicito de variables de entorno para que coincidan con application.yml
        registry.add("POSTGRES_URL", postgres::getJdbcUrl);
        registry.add("POSTGRES_USER", postgres::getUsername);
        registry.add("POSTGRES_PASSWORD", postgres::getPassword);
        
        registry.add("MONGODB_URL", mongo::getReplicaSetUrl);
        registry.add("MONGODB_DB_NAME", () -> "test");
    }
}

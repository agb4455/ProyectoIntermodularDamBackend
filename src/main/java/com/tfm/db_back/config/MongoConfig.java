package com.tfm.db_back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración de MongoDB y ejecución asíncrona para analíticas.
 * Habilita @EnableAsync para que las escrituras en MongoDB no bloqueen el flujo principal.
 *
 * @author Adriana Cabaleiro Álvarez
 */
@Configuration
@EnableAsync
public class MongoConfig {

    @Value("${async.pool-size:4}")
    private int asyncPoolSize;

    @Bean(name = "analyticsTaskExecutor")
    public Executor analyticsTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncPoolSize);
        executor.setMaxPoolSize(asyncPoolSize * 2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("AnalyticsAsync-");
        executor.initialize();
        return executor;
    }
}

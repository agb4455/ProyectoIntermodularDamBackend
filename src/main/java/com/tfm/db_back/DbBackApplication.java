package com.tfm.db_back;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class DbBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbBackApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper()
				.findAndRegisterModules();
	}

	@Bean
	public Flyway flyway(DataSource dataSource) {
		Flyway flyway = Flyway.configure()
				.dataSource(dataSource)
				.locations("classpath:db/migration")
				.baselineOnMigrate(true)
				.load();
		flyway.migrate();
		return flyway;
	}
}

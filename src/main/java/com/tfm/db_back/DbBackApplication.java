package com.tfm.db_back;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Clase principal de la aplicación DB Back.
 * Punto de entrada de Spring Boot para la capa de persistencia.
 *
 * @author Adrián González Blando
 */
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
}

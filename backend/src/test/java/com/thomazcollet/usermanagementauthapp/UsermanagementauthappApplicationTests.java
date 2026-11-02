package com.thomazcollet.usermanagementauthapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(properties = {
		// Mantemos apenas a secret de teste caso o app exija durante a inicialização do
		// bean de JWT
		"api.security.token.secret=segredo-de-teste-com-mais-de-32-caracteres"
})
@Testcontainers
class UsermanagementauthappApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

	@Test
	void contextLoads() {
		// O Testcontainers sobe um container PostgreSQL real via Docker,
		// o Spring Boot se conecta a ele e executa todas as migrations do Flyway,
		// garantindo que o contexto da aplicação carrega sem gambiarras.
	}
}
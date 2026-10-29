package com.thomazcollet.usermanagementauthapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.flyway.enabled=false",
		"spring.jpa.hibernate.ddl-auto=none",
		"spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect",
		// Valores fictícios apenas para o teste (a aplicação não tem mais defaults de
		// segredo)
		"spring.datasource.username=test",
		"spring.datasource.password=test",
		"api.security.token.secret=segredo-de-teste-com-mais-de-32-caracteres"
})
class UsermanagementauthappApplicationTests {

	@Test
	void contextLoads() {
	}

}

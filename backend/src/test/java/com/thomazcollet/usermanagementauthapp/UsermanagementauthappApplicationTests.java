package com.thomazcollet.usermanagementauthapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.flyway.enabled=false",
		"spring.jpa.hibernate.ddl-auto=none",
		"spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect"
})
class UsermanagementauthappApplicationTests {

	@Test
	void contextLoads() {
	}

}

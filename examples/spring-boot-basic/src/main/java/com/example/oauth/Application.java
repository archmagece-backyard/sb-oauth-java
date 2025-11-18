package com.example.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot OAuth Example Application
 *
 * This example demonstrates:
 * - Naver OAuth 2.0 login
 * - User profile retrieval
 * - Session-based token management
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

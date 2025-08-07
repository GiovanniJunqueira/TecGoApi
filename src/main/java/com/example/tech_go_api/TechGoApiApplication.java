package com.example.tech_go_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class TechGoApiApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory("./")
				.ignoreIfMissing()
				.load();
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
		System.setProperty("JWT_ACCESS_TOKEN_DURATION_MINUTES", dotenv.get("JWT_ACCESS_TOKEN_DURATION_MINUTES"));
		System.setProperty("JWT_REFRESH_TOKEN_DURATION_DAYS", dotenv.get("JWT_REFRESH_TOKEN_DURATION_DAYS"));

		SpringApplication.run(TechGoApiApplication.class, args);
	}

}

package com.example.tech_go_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
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

		// Redis
		System.setProperty("REDIS_HOST", dotenv.get("REDIS_HOST"));
		System.setProperty("REDIS_PORT", dotenv.get("REDIS_PORT"));

		// MinIO
		System.setProperty("MINIO_URL", dotenv.get("MINIO_URL"));
		System.setProperty("MINIO_ACCESS_KEY", dotenv.get("MINIO_ACCESS_KEY"));
		System.setProperty("MINIO_SECRET_KEY", dotenv.get("MINIO_SECRET_KEY"));
		System.setProperty("MINIO_BUCKET", dotenv.get("MINIO_BUCKET"));


		SpringApplication.run(TechGoApiApplication.class, args);
	}

}

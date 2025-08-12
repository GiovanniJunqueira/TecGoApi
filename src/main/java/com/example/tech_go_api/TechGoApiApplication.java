package com.example.tech_go_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TechGoApiApplication {

	private static void setPropertySafely(String key, String value) {
		if (value != null) {
			System.setProperty(key, value);
		} else {
			System.err.println("Aviso: Variável de ambiente " + key + " não encontrada no arquivo .env");
		}
	}

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory("./")
				.ignoreIfMissing()
				.load();
		// Postgres
		setPropertySafely("DB_URL", dotenv.get("DB_URL"));
		setPropertySafely("DB_USERNAME", dotenv.get("DB_USERNAME"));
		setPropertySafely("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

		// JWT
		setPropertySafely("JWT_SECRET", dotenv.get("JWT_SECRET"));
		setPropertySafely("JWT_ACCESS_TOKEN_DURATION_MINUTES", dotenv.get("JWT_ACCESS_TOKEN_DURATION_MINUTES"));
		setPropertySafely("JWT_REFRESH_TOKEN_DURATION_DAYS", dotenv.get("JWT_REFRESH_TOKEN_DURATION_DAYS"));

		// Redis
		setPropertySafely("REDIS_HOST", dotenv.get("REDIS_HOST"));
		setPropertySafely("REDIS_PORT", dotenv.get("REDIS_PORT"));

		// MinIO
		setPropertySafely("MINIO_URL", dotenv.get("MINIO_URL"));
		setPropertySafely("MINIO_ACCESS_KEY", dotenv.get("MINIO_ACCESS_KEY"));
		setPropertySafely("MINIO_SECRET_KEY", dotenv.get("MINIO_SECRET_KEY"));
		setPropertySafely("MINIO_BUCKET", dotenv.get("MINIO_BUCKET"));


		SpringApplication.run(TechGoApiApplication.class, args);
	}

}

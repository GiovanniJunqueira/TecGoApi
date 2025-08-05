package com.example.tech_go_api.infra.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("iLearn Lab - API de Quiz")
                        .description("Documentação da API TechGo")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tech Go")
                                .url("https://www.example.com/")));
    }
}
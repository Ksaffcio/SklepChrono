package com.example.javaprojekt.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("basicAuth", // Nazwa schematu uzyta w @SecurityRequirement
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic") // schamat http
                                        .description("Uwierzytelnienie HTTP Basic")))
                .info(new Info()
                        .title("Sklep z Zegarkami API")
                        .version("v1.0")
                        .description("API demonstracyjnego sklepu z zegarkami zbudowanego przy użyciu Spring Boot.")
                        .termsOfService("http://example.com/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
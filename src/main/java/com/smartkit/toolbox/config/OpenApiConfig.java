package com.smartkit.toolbox.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SmartKit Toolbox API")
                .description("Spring Boot RESTful API 模板项目")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("SmartKit Team")
                    .email("team@smartkit.com")));
    }

}
package com.aryak.tts_voice.config;

import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        Contact contact = new Contact();
        contact.setEmail("mailmearyak@gmail.com");
        contact.setUrl("https://github.com/aryak0512");
        contact.setName("Aryak D.");

        return new OpenAPI()

                .info(new Info().title("Automated voice alerts API")
                        .contact(contact)
                        .description("A web service for developers to automate alerts and get phone calls for prod-issues during non-office hours")
                        .version("1.0"));
    }
}


package com.udemycourse.libraryapi.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiConfig {

    @Value("${openapi.title}")
    private String title;
    @Value("${openapi.description}")
    private String description;
    @Value("${openapi.version}")
    private String version;

    @Bean
    public OpenAPI openApiConfiguration() {
        return new OpenAPI().info(new Info()
                .title(title)
                .version(version)
                .description(description)
                .termsOfService("Terms of Service")
                .license(getLicense())
                .contact(getContact()));
    }

    public Contact getContact() {
        Contact contact = new Contact();
        contact.setEmail("email@email.com.br");
        contact.setName("Yuri Ribeiro");
        contact.setUrl("https://github.com/ribYuriRethink");
        contact.setExtensions(Collections.emptyMap());
        return contact;
    }

    public License getLicense() {
        License license = new License();
        license.setName("Apache License, Version 2.0");
        license.setUrl("http://www.apache.org/licenses/LICENSE-2.0");
        license.setExtensions(Collections.emptyMap());
        return license;
    }
}

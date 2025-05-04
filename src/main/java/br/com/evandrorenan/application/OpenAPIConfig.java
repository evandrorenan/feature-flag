package br.com.evandrorenan.application;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${openapi.dev-url:http://localhost:8080}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);

        Contact contact = new Contact();
        contact.setName("Feature Flag Service");
        contact.setEmail("evandrorenan@outlook.com");

        Info info = new Info()
                .title("Feature Flag API")
                .version("1.0.4")
                .contact(contact)
                .description("This API provides feature flag management capabilities");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
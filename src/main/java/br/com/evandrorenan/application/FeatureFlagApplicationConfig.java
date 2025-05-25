package br.com.evandrorenan.application;

import br.com.evandrorenan.domain.ports.out.RequestDetailsLogger;
import br.com.evandrorenan.infra.adapters.log.DefaultRequestDetailsLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
@Slf4j
@NoArgsConstructor
public class FeatureFlagApplicationConfig {

    @Bean
    public ObjectMapper buildObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate buildRestTemplateObject() {
        return new RestTemplate();
    }

    @Bean
    public RequestDetailsLogger buildRequestDetailsLogger() {
        return new DefaultRequestDetailsLogger();
    }

    @Bean
    public TypeReference<Map<String, Object>> buildTypeReference() {
        return new TypeReference<Map<String, Object>>() {};
    }
}
package br.com.evandrorenan.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class FeatureFlagApplicationConfig {

    @Bean
    public ObjectMapper buildObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public TypeReference<Map<String, Object>> buildTypeReference() {
        return new TypeReference<Map<String, Object>>() {};
    }
}

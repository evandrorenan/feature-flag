package br.com.evandrorenan.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

@Configuration
@Slf4j
public class FeatureFlagApplicationConfig {

    private final ConfigurableEnvironment environment;

    public FeatureFlagApplicationConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Bean
    public ObjectMapper buildObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public TypeReference<Map<String, Object>> buildTypeReference() {
        return new TypeReference<Map<String, Object>>() {};
    }

    @Bean
    public CommandLineRunner printEnvironmentVariables() {
        return args -> {
            log.info("Listing all environment variables");
            this.environment.getSystemEnvironment().forEach((k, v) -> {
                log.info("  {}:{}", k, v);
            });
        };
    }
}
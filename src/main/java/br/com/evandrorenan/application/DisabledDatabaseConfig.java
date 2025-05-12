package br.com.evandrorenan.application;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static br.com.evandrorenan.infra.adapters.openfeature.TextFileFeatureFlagAdapter.*;

@Configuration
@Profile("disabled-database")
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class DisabledDatabaseConfig {

    @Bean
    public ResourceFile buildResourceFileName() {
        return new ResourceFile("feature-flags.txt");
    }
}
package br.com.evandrorenan.application;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("disabled-database")
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class DisabledDatabaseConfig {
}
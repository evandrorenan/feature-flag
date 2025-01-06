package br.com.evandrorenan.application;

import br.com.featureflagsdkjava.infra.annotations.EnableFeatureFlag;
import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main class for the Artemis Integration Application.
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication
@EnableFeatureFlag
@ComponentScan({"br.com.evandrorenan"})
@EnableJpaRepositories(basePackages = "br.com.evandrorenan.infra.adapters.persistence")
@EntityScan(basePackages = "br.com.evandrorenan.infra.adapters.persistence")
public class FeatureFlagApplication {

    /**
     * The main method serves as the entry point for the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    @Generated
    public static void main(String[] args) {
        SpringApplication.run(FeatureFlagApplication.class, args);
    }

}
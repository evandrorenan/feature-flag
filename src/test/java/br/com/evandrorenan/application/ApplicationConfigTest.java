package br.com.evandrorenan.application;

import br.com.evandrorenan.infra.adapters.persistence.DataSourceConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationConfigTest {

    @Test
    void disabledDatabaseConfigTest() {
        assertDoesNotThrow(DisabledDatabaseConfig::new);
    }

    @Test
    void openAPIConfigTest() {
        assertDoesNotThrow(() -> new OpenAPIConfig().myOpenAPI());
    }

    @Test
    void featureFlagApplicationConfigTest() {
        assertDoesNotThrow(() -> {
            FeatureFlagApplicationConfig featureFlagApplicationConfig = new FeatureFlagApplicationConfig();
            featureFlagApplicationConfig.buildObjectMapper();
            featureFlagApplicationConfig.buildTypeReference();
            featureFlagApplicationConfig.buildRestTemplateObject();
            featureFlagApplicationConfig.buildRequestDetailsLogger();
        });
    }

    @Test
    void dataSourceConfigTest() {
        assertDoesNotThrow(() -> {
            new DataSourceConfig().dataSource();
        });
    }
}
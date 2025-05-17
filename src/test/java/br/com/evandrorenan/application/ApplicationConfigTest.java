package br.com.evandrorenan.application;

import br.com.evandrorenan.infra.adapters.persistence.DataSourceConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ApplicationConfigTest {

    public static final String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    public static final String USERNAME = "sa";
    public static final int MAX_POOL_SIZE = 10;
    public static final int MIN_POOL_SIZE = 5;
    public static final int IDLE_TIMEOUT = 30000;
    public static final int MAX_LIFETIME = 1800000;
    public static final int CONNECTION_TIMEOUT = 2000;
    public static final String POOLNAME = "HikariCP";

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
            new DataSourceConfig(JDBC_URL, USERNAME, "", MAX_POOL_SIZE, MIN_POOL_SIZE,
                    IDLE_TIMEOUT, MAX_LIFETIME, CONNECTION_TIMEOUT, POOLNAME)
                    .dataSource();
        });
    }
}
package br.com.evandrorenan.infra.adapters.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:5}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.idle-timeout:30000}")
    private int idleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime:1800000}")
    private int maxLifetime;

    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private int connectionTimeout;

    @Value("${spring.datasource.hikari.pool-name:HikariCP}")
    private String poolName;

    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl             (jdbcUrl);
        hikariConfig.setUsername            (username);
        hikariConfig.setPassword            (password);
        hikariConfig.setMaximumPoolSize     (maximumPoolSize);
        hikariConfig.setMinimumIdle         (minimumIdle);
        hikariConfig.setIdleTimeout         (idleTimeout);
        hikariConfig.setMaxLifetime         (maxLifetime);
        hikariConfig.setConnectionTimeout   (connectionTimeout);
        hikariConfig.setPoolName            (poolName);

        return new HikariDataSource(hikariConfig);
    }
}
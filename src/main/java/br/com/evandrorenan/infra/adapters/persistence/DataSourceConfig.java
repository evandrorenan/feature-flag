package br.com.evandrorenan.infra.adapters.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Slf4j
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
        log.info("jdbcUrl.............: {}", jdbcUrl);
        log.info("username............: {}", username);
        log.info("password............: {}", password);
        log.info("maximumPoolSize.....: {}", maximumPoolSize);
        log.info("minimumIdle.........: {}", minimumIdle);
        log.info("idleTimeout.........: {}", idleTimeout);
        log.info("setMaxLifetime......: {}", maxLifetime);
        log.info("setConnectionTimeout: {}", connectionTimeout);
        log.info("poolName............: {}", poolName);

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
package br.com.evandrorenan.infra.adapters.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "br.com.evandrorenan.infra.adapters.persistence")
@EntityScan(basePackages = "br.com.evandrorenan.infra.adapters.persistence")
@Slf4j
@Profile("!disabled-database")
public class DataSourceConfig {


    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final int maximumPoolSize;
    private final int minimumIdle;
    private final int maxLifetime;
    private final int idleTimeout;
    private final int connectionTimeout;
    private final String poolName;

    @Autowired
    public DataSourceConfig(
        @Value("${spring.datasource.url}")                             String jdbcUrl,
        @Value("${spring.datasource.username}")                        String username,
        @Value("${spring.datasource.password}")                        String password,
        @Value("${spring.datasource.hikari.maximum-pool-size:10}")     int maximumPoolSize,
        @Value("${spring.datasource.hikari.minimum-idle:5}")           int minimumIdle,
        @Value("${spring.datasource.hikari.idle-timeout:30000}")       int idleTimeout,
        @Value("${spring.datasource.hikari.max-lifetime:1800000}")     int maxLifetime,
        @Value("${spring.datasource.hikari.connection-timeout:30000}") int connectionTimeout,
        @Value("${spring.datasource.hikari.pool-name:HikariCP}")       String poolName) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.maximumPoolSize = maximumPoolSize;
        this.minimumIdle = minimumIdle;
        this.idleTimeout = idleTimeout;
        this.maxLifetime = maxLifetime;
        this.connectionTimeout = connectionTimeout;
        this.poolName = poolName;
    }

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
package com.example.javamvcthymeleaf.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    private static final String H2_URL = "jdbc:h2:mem:propostasdb";

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource(Environment environment) {
        String rawUrl = environment.getProperty("POSTGRES_URL");

        String jdbcUrl = toJdbcUrl(rawUrl);
        String username = extractUser(rawUrl);
        String password = extractPassword(rawUrl);
        String driverClassName = isH2(jdbcUrl) ? "org.h2.Driver" : "org.postgresql.Driver";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        if (username != null && !username.isBlank()) {
            config.setUsername(username);
        }
        if (password != null) {
            config.setPassword(password);
        }
        config.setDriverClassName(driverClassName);

        return new HikariDataSource(config);
    }

    private String toJdbcUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return H2_URL;
        }

        if (rawUrl.startsWith("jdbc:")) {
            return rawUrl;
        }

        if (rawUrl.startsWith("postgresql://") || rawUrl.startsWith("postgres://")) {
            return "jdbc:" + rawUrl;
        }

        return rawUrl;
    }

    private String extractUser(String rawUrl) {
        URI uri = parseUri(rawUrl);
        if (uri == null || uri.getUserInfo() == null || uri.getUserInfo().isBlank()) {
            return null;
        }

        int separatorIndex = uri.getUserInfo().indexOf(':');
        if (separatorIndex < 0) {
            return uri.getUserInfo();
        }

        return uri.getUserInfo().substring(0, separatorIndex);
    }

    private String extractPassword(String rawUrl) {
        URI uri = parseUri(rawUrl);
        if (uri == null || uri.getUserInfo() == null || uri.getUserInfo().isBlank()) {
            return null;
        }

        int separatorIndex = uri.getUserInfo().indexOf(':');
        if (separatorIndex < 0 || separatorIndex == uri.getUserInfo().length() - 1) {
            return null;
        }

        return uri.getUserInfo().substring(separatorIndex + 1);
    }

    private URI parseUri(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }

        try {
            return new URI(rawUrl);
        } catch (URISyntaxException ex) {
            return null;
        }
    }

    private boolean isH2(String jdbcUrl) {
        return jdbcUrl != null && jdbcUrl.startsWith("jdbc:h2:");
    }
}
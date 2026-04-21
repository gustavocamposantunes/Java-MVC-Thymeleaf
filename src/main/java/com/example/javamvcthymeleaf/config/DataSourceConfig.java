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

        String normalizedUrl = stripJdbcPrefix(rawUrl);
        URI uri = parseUri(normalizedUrl);

        if (uri == null) {
            return rawUrl.startsWith("jdbc:") ? rawUrl : "jdbc:" + rawUrl;
        }

        if (!isPostgresScheme(uri.getScheme())) {
            return rawUrl;
        }

        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://");

        if (uri.getHost() != null) {
            jdbcUrl.append(uri.getHost());
        }

        if (uri.getPort() != -1) {
            jdbcUrl.append(":").append(uri.getPort());
        }

        if (uri.getRawPath() != null) {
            jdbcUrl.append(uri.getRawPath());
        }

        if (uri.getRawQuery() != null) {
            jdbcUrl.append("?").append(uri.getRawQuery());
        }

        return jdbcUrl.toString();
    }

    private String extractUser(String rawUrl) {
        URI uri = parseUri(stripJdbcPrefix(rawUrl));
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
        URI uri = parseUri(stripJdbcPrefix(rawUrl));
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

    private String stripJdbcPrefix(String rawUrl) {
        if (rawUrl == null) {
            return null;
        }

        return rawUrl.startsWith("jdbc:") ? rawUrl.substring(5) : rawUrl;
    }

    private boolean isPostgresScheme(String scheme) {
        return "postgresql".equalsIgnoreCase(scheme) || "postgres".equalsIgnoreCase(scheme);
    }

    private boolean isH2(String jdbcUrl) {
        return jdbcUrl != null && jdbcUrl.startsWith("jdbc:h2:");
    }
}
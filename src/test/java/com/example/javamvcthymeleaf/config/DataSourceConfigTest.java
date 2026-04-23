package com.example.javamvcthymeleaf.config;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import javax.sql.DataSource;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataSourceConfigTest {

    private final DataSourceConfig config = new DataSourceConfig();

    @Test
    void dataSourceDeveUsarH2QuandoNaoHaPostgresUrl() {
        MockEnvironment environment = new MockEnvironment();

        DataSource dataSource = config.dataSource(environment);
        HikariDataSource hikari = (HikariDataSource) dataSource;

        assertEquals("jdbc:h2:mem:propostasdb", hikari.getJdbcUrl());
        assertEquals("org.h2.Driver", hikari.getDriverClassName());
        assertNull(hikari.getUsername());
        hikari.close();
    }

    @Test
    void metodosPrivadosDevemConverterPostgresUrlEExtrairCredenciais() throws Exception {
        String rawUrl = "postgres://user:pass@localhost:5432/minha_db?sslmode=require";

        String jdbcUrl = invokeStringMethod("toJdbcUrl", rawUrl);
        String username = invokeStringMethod("extractUser", rawUrl);
        String password = invokeStringMethod("extractPassword", rawUrl);

        assertEquals("jdbc:postgresql://localhost:5432/minha_db?sslmode=require", jdbcUrl);
        assertEquals("user", username);
        assertEquals("pass", password);
    }

    @Test
    void dataSourceDevePreservarJdbcNaoPostgres() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("POSTGRES_URL", "jdbc:h2:mem:testdb");

        DataSource dataSource = config.dataSource(environment);
        HikariDataSource hikari = (HikariDataSource) dataSource;

        assertTrue(hikari.getJdbcUrl().startsWith("jdbc:h2:mem:testdb"));
        assertEquals("org.h2.Driver", hikari.getDriverClassName());
        hikari.close();
    }

    private String invokeStringMethod(String methodName, String value) throws Exception {
        Method method = DataSourceConfig.class.getDeclaredMethod(methodName, String.class);
        method.setAccessible(true);
        Object result = method.invoke(config, value);
        assertNotNull(result);
        return (String) result;
    }
}

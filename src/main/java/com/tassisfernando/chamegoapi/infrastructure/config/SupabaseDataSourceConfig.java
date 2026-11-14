package com.tassisfernando.chamegoapi.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
public class SupabaseDataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(SupabaseDataSourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String original = properties.getUrl();
        String url = JdbcUrlSupabase.normalizar(original);
        if (original != null && !original.equals(url)) {
            log.info("JDBC ajustada para o pooler Session do Supabase (porta 5432) e prepareThreshold=0.");
        }

        HikariConfig hikari = new HikariConfig();
        hikari.setJdbcUrl(url);
        hikari.setUsername(properties.getUsername());
        hikari.setPassword(properties.getPassword());
        if (properties.getDriverClassName() != null) {
            hikari.setDriverClassName(properties.getDriverClassName());
        }
        // Plano free/starter do Supabase tem poucas conexões no modo Session.
        hikari.setMaximumPoolSize(3);
        hikari.setMinimumIdle(0);
        hikari.setConnectionTimeout(30_000);
        hikari.setIdleTimeout(60_000);
        hikari.setMaxLifetime(600_000);
        hikari.addDataSourceProperty("prepareThreshold", "0");
        hikari.addDataSourceProperty("preparedStatementCacheQueries", "0");
        return new HikariDataSource(hikari);
    }
}

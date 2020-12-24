package demos.web;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.logging.Logger;

@Configuration
@RequiredArgsConstructor
public class Config {
    private static final Logger LOG = Logger.getLogger("Config");

    @Bean
    @RefreshScope
    public DataSource dataSource(Environment environment, DBConfig dbConfig) throws SQLException {
        // Use programmatic way to avoid possible crash when env is missing
        String RDS_PASSWORD = environment.getProperty("RDS_PASSWORD", "");
        LOG.info(String.format("RDS_PASSWORD: `%s`", RDS_PASSWORD));
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class).build();
        dataSource.setJdbcUrl(dbConfig.getJdbcUrl());
        dataSource.setUsername(dbConfig.getUsername());
        dataSource.setPoolName(dbConfig.getPoolName());
        dataSource.setMaxLifetime(dbConfig.getMaxLifetime());
        try {
            dataSource.setPassword(RDS_PASSWORD);
            dataSource.getConnection();
            LOG.info("build datasource bean with env succeeded");
        } catch (Exception e) {
            // fallback to spring yml config
            LOG.severe("build datasource bean with env failed");
            dataSource.setPassword(dbConfig.getPassword());
            dataSource.getConnection();
            // getConnection should not failed, and we can not recover from this failure
            LOG.info("build dataSource bean with spring yml succeeded");
        }
        return dataSource;
    }
}

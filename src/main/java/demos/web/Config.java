package demos.web;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.logging.Logger;

@Configuration
@RequiredArgsConstructor
public class Config {
    private static final Logger LOG = Logger.getLogger("Config");

    @Bean
    public DataSource dataSource(Environment environment, DBConfig dbConfig) {
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
            dataSource.setPassword(dbConfig.getPassword());
            dataSource.getConnection();
            LOG.info("build datasource bean with spring yml succeeded");
        } catch (Exception e) {
            // fallback to env variable
            LOG.severe("build datasource bean with spring yml failed");
            dataSource.setPassword(RDS_PASSWORD);
            // getConnection should not failed, and we can not recover from failure
            LOG.info("build dataSource bean with env succeeded");
        }
        return dataSource;
    }
}

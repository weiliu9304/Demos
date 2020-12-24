package demos.web;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("spring.datasource.test")
@Configuration
@Setter
@Getter
@ToString
public class DBConfig {
    private String jdbcUrl;
    private String username;
    private String password;
    private String poolName;
    private Long maxLifetime;
}

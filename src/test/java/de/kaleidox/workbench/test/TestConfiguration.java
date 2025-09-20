package de.kaleidox.workbench.test;

import de.kaleidox.workbench.model.jpa.representant.User;
import org.h2.Driver;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TestConfiguration {
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:test")
                .username("sa")
                .password("")
                .driverClassName(Driver.class.getCanonicalName())
                .build();
    }

    @Bean
    public TestRestTemplate rest() {
        return new TestRestTemplate(User.DEV.getUsername(), "");
    }
}

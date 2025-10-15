package de.kaleidox.workbench;

import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.kaleidox.workbench.model.config.AppConfig;
import lombok.extern.java.Log;
import org.comroid.api.func.util.Debug;
import org.mariadb.jdbc.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Objects;

@Log
@ImportResource({ "classpath:beans.xml" })
@EntityScan(basePackages = "de.kaleidox.workbench.model.jpa")
@EnableJpaRepositories(basePackages = "de.kaleidox.workbench.repo")
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class WorkbenchApplication implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    public static void main(String[] args) {
        SpringApplication.run(WorkbenchApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        var mapper = new ObjectMapper(new JsonFactoryBuilder() {{
            enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION);
        }}.build());
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public File configDir() {
        return new File("/srv/workbench/");
    }

    @Bean
    public File configFile(@Autowired File configDir) throws URISyntaxException {
        var file = new File(configDir, "config.json");
        if (file.exists()) return file;
        log.warning("Config file %s does not exist, falling back to default config resource...".formatted(file.getAbsolutePath()));
        return new File(Objects.requireNonNull(WorkbenchApplication.class.getResource("/config.json"),
                "Unable to load default config").toURI());
    }

    @Bean
    public AppConfig config(@Autowired File configFile, @Autowired ObjectMapper objectMapper) throws IOException {
        return objectMapper.readValue(configFile, AppConfig.class);
    }

    @Bean
    @Order
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource(@Autowired AppConfig config) {
        var db = config.database();
        return DataSourceBuilder.create()
                .url(db.uri())
                .username(db.username())
                .password(db.password())
                .driverClassName(Driver.class.getCanonicalName())
                .build();
    }

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        if (Debug.isDebug()) factory.setPort(8080);
        else {
            factory.setPort(42069);
            factory.setAddress(InetAddress.getLoopbackAddress());
        }
    }
}

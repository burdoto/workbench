package de.kaleidox.workbench;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kaleidox.workbench.model.config.AppConfig;
import lombok.extern.java.Log;
import org.mariadb.jdbc.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

@Log
@SpringBootApplication
@ImportResource({ "classpath:beans.xml" })
@EnableJpaRepositories(basePackages = "de.kaleidox.workbench.repo")
@EntityScan(basePackages = "de.kaleidox.workbench.model.jpa")
public class WorkbenchApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkbenchApplication.class, args);
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
    public DataSource dataSource(@Autowired AppConfig config) {
        var db = config.database();
        return DataSourceBuilder.create()
                .url(db.uri())
                .username(db.username())
                .password(db.password())
                .driverClassName(Driver.class.getCanonicalName())
                .build();
    }
}

package com.smartkit.toolbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.File;

@Configuration
public class SQLiteConfig {

    private static final String DB_DIR = "data";
    private static final String DB_FILE = "toolbox.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_FILE;

    @Bean
    public DataSource dataSource() {
        File dbDir = new File(DB_DIR);
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl(DB_URL);

        return dataSource;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("sql/schema.sql"));
        // 设置continueOnError避免重复执行报错
        populator.setContinueOnError(true);

        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
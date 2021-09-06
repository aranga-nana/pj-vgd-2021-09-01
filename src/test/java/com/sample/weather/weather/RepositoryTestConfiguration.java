package com.sample.weather.weather;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * set up memory based data source for test provile
 */
@Configuration
@Profile("test")
public class RepositoryTestConfiguration {
    @Primary // make sure this get injected when running test
    @Bean
    public DataSource dataSource() {

        // Setup a data source for our tests
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE;");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        return dataSource;
    }
}

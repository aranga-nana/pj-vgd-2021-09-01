package com.sample.weather.weather.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.sample.weather.weather.domain.Weather;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Optional;

@ExtendWith({DBUnitExtension.class, SpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WeatherRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private WeatherRepository weatherRepository;

    public ConnectionHolder getConnectionHolder() {
        // Return a function that retrieves a connection from our data source
        return () -> dataSource.getConnection();
    }
    @Test
    @DataSet("weather.yml")
    @DisplayName("findByCountryAndCity() - success")
    void findByCountryAndCitySuccess(){

        Optional<Weather> found = weatherRepository.findByCountryAndCity("au","melbourne");
        assertTrue(found.isPresent());

    }
    @Test
    @DataSet("weather.yml")
    @DisplayName("findByCountryAndCity() - success (case check)")
    void findByCountryAndCityCaseSuccess(){

        Optional<Weather> found = weatherRepository.findByCountryAndCity("AU","Melbourne");
        assertTrue(found.isPresent());
    }

    @Test
    @DataSet("weather.yml")
    @DisplayName("findByCountryAndCity() not found - Fail")
    void findByCountryAndCityNotFound(){

        Optional<Weather> found = weatherRepository.findByCountryAndCity("us","New York");
        assertFalse(found.isPresent());
    }


}

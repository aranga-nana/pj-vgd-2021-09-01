package com.sample.weather.weather.services.integration;

import static org.junit.jupiter.api.Assertions.*;


import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.sample.weather.weather.common.OutputResult;
import com.sample.weather.weather.domain.Weather;
import com.sample.weather.weather.repository.WeatherRepository;
import com.sample.weather.weather.services.WeatherService;
import com.sample.weather.weather.services.dto.WeatherInfoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@ExtendWith({DBUnitExtension.class, SpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WeatherServiceImplIntegratedTest {


    @Autowired
    private DataSource dataSource;

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private WeatherService weatherService;

    public ConnectionHolder getConnectionHolder() {
        // Return a function that retrieves a connection from our data source
        return () -> dataSource.getConnection();
    }

    @Test
    @DisplayName("getWeather() - from db ")
    void getWeatherFromDb(){
        // insert
        Weather weather = new Weather();
        weather.setCountry("uk");
        weather.setCity("london");
        weather.setDescription("weather from db");
        weather.setUpdated(new Date());
        weatherRepository.save(weather);

        OutputResult<WeatherInfoDTO> oW = weatherService.getWeather("uk","london");
        assertTrue(oW.isSuccess());
        assertEquals("weather from db",oW.getData().getDescription());

    }
    @Test
    @DisplayName("getWeather() - from api (not found in db,save to db as new record) ")
    void getWeatherFromApiNotFoundInDbAndSaveToDb(){

        // make sure its not exists
        Optional<Weather> found = weatherRepository.findByCountryAndCity("au","melbourne");
        assertFalse(found.isPresent());
        Date now = new Date();
        OutputResult<WeatherInfoDTO> oW = weatherService.getWeather("au","melbourne");
        assertTrue(oW.isSuccess());
        assertNotNull(oW.getData().getDescription());
        // check if its getting saved as well;
        found = weatherRepository.findByCountryAndCity("au","melbourne");
        assertTrue(found.isPresent());
        Instant from = found.get().getUpdated().toInstant();
        Instant to = now.toInstant();
        Duration duration = Duration.between(from,to);
        assertTrue(duration.toMinutes() == 0,"Check duration is 0 ( just inserted)");


    }
    @Test
    @DisplayName("getWeather() - from api ( current db record is pass TTL) ")
    void getWeatherFromApiTTL(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,-10);
        // insert
        Weather weather = new Weather();
        weather.setCountry("uk");
        weather.setCity("london");
        weather.setDescription("weather from db");
        weather.setUpdated(calendar.getTime()); // last updated -10 hours a go
        weatherRepository.save(weather);

        OutputResult<WeatherInfoDTO> oW = weatherService.getWeather("uk","london");
        assertTrue(oW.isSuccess());
        assertNotEquals("weather from db",oW.getData().getDescription());

    }
}

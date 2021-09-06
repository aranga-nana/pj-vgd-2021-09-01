package com.sample.weather.weather.services.integration;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.junit5.DBUnitExtension;
import com.sample.weather.weather.domain.ApiKey;
import com.sample.weather.weather.domain.Weather;
import com.sample.weather.weather.repository.ApiKeyRepository;
import com.sample.weather.weather.repository.WeatherRepository;
import com.sample.weather.weather.services.ApiKeyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test (mimic actual user requests and evoke controller)
 */
@ExtendWith({DBUnitExtension.class, SpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class WeatherApiIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private ApiKeyService keyService;


    private final String validApiKey = "ZFhObGNqQkFkMlZoZEdobGNpMWxlR0Z0Y0d4bExtTnZiUzVoZFE9PS4xNjMwODk1ODc0MDQ4LjE2MzA4OTU4NzQwNDguNTIyMUJCQTQyNTU2RTcwNkQ5MEJGNTQ2NUE3M0JEMzQ=";

    public ConnectionHolder getConnectionHolder() {
        // Return a function that retrieves a connection from our data source
        return () -> dataSource.getConnection();
    }

    @Test
    @DisplayName("/api/weather/current?country=uk&city=London - SUCCESS ( from db)")
    void getWeatherFromDbSuccess() throws Exception{

        // api key

        ApiKey key = new ApiKey();
        key.setKey(validApiKey);
        key.setInvocations(1);
        key.setUpdated(new Date());
        key.setEmail("test@test.com.au");
        apiKeyRepository.save(key);

        // set up weather record
        Weather weather = new Weather();
        weather.setUpdated(new Date());
        weather.setCountry("uk");
        weather.setCity("London");
        weather.setDescription("Sunny:fromDB");
        Weather saveWeather = weatherRepository.save(weather);


        mockMvc.perform(get("/api/weather/current?")
                .param("country","uk")
                .param("city","London")
                .param("apiKey",validApiKey))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description",is("Sunny:fromDB")));
    }
    @Test
    @DisplayName("/api/weather/current?country=AU&city=Melbourne - SUCCESS (not found in db , get from open weather)")
    void getWeatherFromOpenWeatherService() throws Exception{

        // api key

        ApiKey key = new ApiKey();
        key.setKey(validApiKey);
        key.setInvocations(1);
        key.setUpdated(new Date());
        key.setEmail("test@test.com.au");
        apiKeyRepository.save(key);

        // record not exist check
        Optional<Weather> found = weatherRepository.findByCountryAndCity("AU","Melbourne");
        assertFalse(found.isPresent());


        mockMvc.perform(get("/api/weather/current?")
                .param("country","AU")
                .param("city","Melbourne")
                .param("apiKey",validApiKey))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description",not("Sunny:fromDB")));
    }
    @Test
    @DisplayName("/api/weather/current?country=NZ&city=Auckland - SUCCESS (record from OpenWeather due to wether record TTL expire")
    void getWeatherFromOpenWeatherServiceTTLExpires() throws Exception{

        // api key

        ApiKey key = new ApiKey();
        key.setKey(validApiKey);
        key.setInvocations(1);
        key.setUpdated(new Date());
        key.setEmail("test3@test.com.au");
        apiKeyRepository.save(key);


        // ttl set to 5 hours ( property in application.yml)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,-6);

        Weather weather = new Weather();
        weather.setUpdated(calendar.getTime()); // TTL expire
        weather.setCountry("NZ");
        weather.setCity("Auckland");
        weather.setDescription("Sunny:fromDB");
        Weather saveWeather = weatherRepository.save(weather);

        mockMvc.perform(get("/api/weather/current?")
                .param("country","NZ")
                .param("city","Auckland")
                .param("apiKey",validApiKey))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description",not("Sunny:fromDB")));
    }
    @Test
    @DisplayName("/api/weather/current?country=PP&city=Anucp - FAIL (invalid parameters)")
    void getWeatherFromOpenWeatherServiceTInvalidCountryCity() throws Exception{

        // api key

        ApiKey key = new ApiKey();
        key.setKey(validApiKey);
        key.setInvocations(1);
        key.setUpdated(new Date());
        key.setEmail("test4@test.com.au");
        apiKeyRepository.save(key);


        mockMvc.perform(get("/api/weather/current?")
                .param("country","PP")
                .param("city","Auck1")
                .param("apiKey",validApiKey))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));

    }
    @Test
    @DisplayName("/api/weather/current?country=AU&city=Sydney - FAIL (Throttle)")
    void getWeatherFromOpenWeatherServiceThrottle() throws Exception{

        // api key

        ApiKey key = new ApiKey();
        key.setKey(validApiKey);
        key.setInvocations(5);
        key.setUpdated(new Date());
        key.setEmail("test5@test.com.au");
        apiKeyRepository.save(key);


        mockMvc.perform(get("/api/weather/current?")
                .param("country","AU")
                .param("city","Sydney")
                .param("apiKey",validApiKey))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(429));

    }
    @Test
    @DisplayName("/api/weather/current?country=AU&city=Sydney - FAIL (Invalid key)")
    void getWeatherFromOpenWeatherServiceInvalidApiKey() throws Exception{

        mockMvc.perform(get("/api/weather/current?")
                .param("country","AU")
                .param("city","Sydney")
                .param("apiKey","ewrewoerwioewrouweriuowreuioueriuuiweroui"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("/api/weather/current?country=AU&city=Sydney - FAIL (expired key)")
    void getWeatherFromOpenWeatherServiceExpireApiKey() throws Exception {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,-2);
        String expiredKey = keyService.generateNewKey("test@test.com",5,calendar.getTime());


        mockMvc.perform(get("/api/weather/current?")
                .param("country","AU")
                .param("city","Sydney")
                .param("apiKey",expiredKey))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden());
    }



}

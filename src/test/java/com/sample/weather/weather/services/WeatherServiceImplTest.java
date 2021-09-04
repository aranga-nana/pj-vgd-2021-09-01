package com.sample.weather.weather.services;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sample.weather.weather.common.ErrorCode;
import com.sample.weather.weather.common.OutputResult;
import com.sample.weather.weather.domain.Weather;
import com.sample.weather.weather.repository.WeatherRepository;
import com.sample.weather.weather.services.dto.WeatherInfoDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
class WeatherServiceImplTest {

    @MockBean
    private WeatherRepository repository;

    @MockBean
    private OpenWeatherApiService apiService;

    @Autowired
    private WeatherService weatherService;

    @Test
    @DisplayName("getWeather from DB - SUCCESS")
    void getWeatherFromDbSuccess() {
        Weather mocWeather = new Weather();
        mocWeather.setCity("Melbourne");
        mocWeather.setCountry("Australia");
        mocWeather.setDescription("Sunny");
        mocWeather.setUpdated(new Date());
        doReturn(Optional.of(mocWeather)).when(repository).findByCountryAndCity("Australia", "Melbourne");

        OutputResult<WeatherInfoDTO> found = weatherService.getWeather("Australia","Melbourne");
        assertTrue(found.isSuccess(),"GET WEATHER - FROM DB - SUCCESS");
        assertEquals("Sunny",found.getData().getDescription());
    }
    @Test
    @DisplayName("getWeather: Invalid country/city  - FAILED")
    void getWeatherInvalidCountryAndCity() {
        Weather mocWeather = new Weather();
        mocWeather.setCity("Melbourne");
        mocWeather.setCountry("Australia");
        mocWeather.setDescription("Sunny");
        doReturn(Optional.empty()).when(repository).findByCountryAndCity("Australia", "Akaroa");
        doReturn(Optional.empty()).when(apiService).getWeatherDescription("Australia", "Akaroa");

        OutputResult<WeatherInfoDTO> found = weatherService.getWeather("Australia","Akaroa");
        assertFalse(found.isSuccess());

    }
    @Test
    @DisplayName("getWeather: from the api service (not found in db) - SUCCESS")
    void getWeatherFromApiNotFoundInDb() {

        doReturn(Optional.empty()).when(repository).findByCountryAndCity("Australia", "Sydney");
        doReturn(Optional.of("Sunny")).when(apiService).getWeatherDescription("Australia", "Sydney");

        OutputResult<WeatherInfoDTO> found = weatherService.getWeather("Australia","Sydney");
        assertTrue(found.isSuccess(),"GET WEATHER - FROM DB ");
        assertEquals("Sunny",found.getData().getDescription());
    }
    @Test
    @DisplayName("getWeather: from the api service (ttl of weather records expires) - SUCCESS")
    void getWeatherFromApiTTLExpired() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,-6);
        Weather mocWeather = new Weather();
        mocWeather.setCity("Melbourne");
        mocWeather.setCountry("Australia");
        mocWeather.setDescription("Sunny");
        mocWeather.setUpdated(calendar.getTime()); // expired

        doReturn(Optional.of(mocWeather)).when(repository).findByCountryAndCity("Australia", "Sydney");
        doReturn(Optional.of("Partly Cloudy")).when(apiService).getWeatherDescription("Australia", "Sydney");

        OutputResult<WeatherInfoDTO> found = weatherService.getWeather("Australia","Sydney");
        assertTrue(found.isSuccess());
        assertEquals("Partly Cloudy",found.getData().getDescription());
    }
    @Test
    @DisplayName("getWeather: from the api service with error - FAILED")
    void getWeatherApiError() {

        doReturn(Optional.empty()).when(repository).findByCountryAndCity("Australia", "Sydney");
        doThrow(IllegalStateException.class).when(apiService).getWeatherDescription("Australia", "Sydney");

        OutputResult<WeatherInfoDTO> found = weatherService.getWeather("Australia","Sydney");
        assertFalse(found.isSuccess());
        assertEquals(ErrorCode.internalServiceError,found.getErrorCode());
    }

}

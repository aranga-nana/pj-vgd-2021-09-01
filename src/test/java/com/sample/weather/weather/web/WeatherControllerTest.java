package com.sample.weather.weather.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
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
import com.sample.weather.weather.services.ApiKeyService;
import com.sample.weather.weather.services.WeatherService;
import com.sample.weather.weather.services.dto.WeatherInfoDTO;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class WeatherControllerTest {

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private ApiKeyService apiKeyService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/weather/current?country=Australia&city=melbourne&apiKey=12838848 - success")
    void testGetWeatherSuccess() throws Exception {
        OutputResult mockApiValidationResult = new OutputResult().withSuccess(true);

        WeatherInfoDTO mockInfo = new WeatherInfoDTO();
        mockInfo.setCity("Melbourne");
        mockInfo.setCountry("Australia");
        mockInfo.setDescription("sunny");
        OutputResult<WeatherInfoDTO>  mockWeatherResult = new
                OutputResult<WeatherInfoDTO>().withData(mockInfo).withSuccess(true);

        doReturn(mockApiValidationResult).when(apiKeyService).validate(any());
        doReturn(mockWeatherResult).when(weatherService).getWeather("Australia","Melbourne");
        mockMvc.perform(get("/api/weather/current?")
                        .param("country","Australia")
                        .param("city","Melbourne")
                        .param("apiKey","aiweiweie"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city",is("Melbourne")))
                .andExpect(jsonPath("$.country",is("Australia")))
                .andExpect(jsonPath("$.description",is("sunny")));

       // Assert.assertTrue(true);
    }
    @Test
    @DisplayName("GET /api/weather/current?country=Australia&city=melbourne&apiKey=12838848 - Throttle ")
    void testGetWeatherThrottleFail() throws Exception {
        OutputResult mockApiValidationResult = new OutputResult().withSuccess(false).withErrorCode(ErrorCode.rateExceeded);

        WeatherInfoDTO mockInfo = new WeatherInfoDTO();
        mockInfo.setCity("Melbourne");
        mockInfo.setCountry("Australia");
        mockInfo.setDescription("sunny");
        OutputResult<WeatherInfoDTO>  mockWeatherResult = new
                OutputResult<WeatherInfoDTO>().withData(mockInfo).withSuccess(true);

        doReturn(mockApiValidationResult).when(apiKeyService).validate(any());
        doReturn(mockWeatherResult).when(weatherService).getWeather("Australia","Melbourne");
        mockMvc.perform(get("/api/weather/current?")
                .param("country","Australia")
                .param("city","Melbourne")
                .param("apiKey","aiweiweie"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(429));
    }
    @Test
    @DisplayName("GET /api/weather/current?country=Australia&city=melbourne&apiKey=12838848 - Invalid api key ")
    void testGetWeatherInvalidApiFail() throws Exception {
        OutputResult mockApiValidationResult = new OutputResult().withSuccess(false).withErrorCode(ErrorCode.forbidden);

        WeatherInfoDTO mockInfo = new WeatherInfoDTO();
        mockInfo.setCity("Melbourne");
        mockInfo.setCountry("Australia");
        mockInfo.setDescription("sunny");
        OutputResult<WeatherInfoDTO>  mockWeatherResult = new
                OutputResult<WeatherInfoDTO>().withData(mockInfo).withSuccess(true);

        doReturn(mockApiValidationResult).when(apiKeyService).validate(any());
        doReturn(mockWeatherResult).when(weatherService).getWeather("Australia","Melbourne");
        mockMvc.perform(get("/api/weather/current?")
                .param("country","Australia")
                .param("city","Melbourne")
                .param("apiKey","aiweiweie"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden());
    }

}

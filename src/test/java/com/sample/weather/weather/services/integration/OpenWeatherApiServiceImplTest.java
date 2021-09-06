package com.sample.weather.weather.services.integration;

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
import static org.junit.jupiter.api.Assertions.*;

import com.sample.weather.weather.services.OpenWeatherApiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class OpenWeatherApiServiceImplTest {

    @Autowired
    private OpenWeatherApiService apiService;

    @Test
    @DisplayName("getWeatherDescription() - success")
    void getWeatherDescriptionSuccess() {
        Optional<String> description = apiService.getWeatherDescription("UK","London");
        System.out.println(description);
        assertTrue(description.isPresent());
    }
    @DisplayName("getWeatherDescription() (invalid param) - Failed")
    @Test
    void getWeatherDescriptionInvalidCountryCity() {
        Optional<String> description = apiService.getWeatherDescription("AU","Mel1");
        System.out.println(description);
        assertFalse(description.isPresent());
    }


}

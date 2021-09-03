package com.sample.weather.weather.services;

import com.sample.weather.weather.common.OutputResult;
import com.sample.weather.weather.services.dto.WeatherInfoDTO;

public interface WeatherService {

    OutputResult<WeatherInfoDTO> getWeather(String country, String city);
}

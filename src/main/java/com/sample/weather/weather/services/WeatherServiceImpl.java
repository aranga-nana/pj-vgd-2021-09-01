package com.sample.weather.weather.services;

import com.sample.weather.weather.common.OutputResult;
import com.sample.weather.weather.services.dto.WeatherInfoDTO;
import org.springframework.stereotype.Service;

@Service
public class WeatherServiceImpl implements WeatherService {
    @Override
    public OutputResult<WeatherInfoDTO> getWeather(String country, String city) {
        return new OutputResult<WeatherInfoDTO>().withSuccess(false);
    }
}

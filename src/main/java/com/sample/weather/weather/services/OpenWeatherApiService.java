package com.sample.weather.weather.services;

import java.util.Optional;

public interface OpenWeatherApiService {

    Optional<String> getWeatherDescription(String country,String city);
}

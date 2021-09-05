package com.sample.weather.weather.services.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * only interested in the weather property & name for validation
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherMapResponse {
    private List<Map<String, Object>> weather = new ArrayList<>();
    private String name;

    public List<Map<String, Object>> getWeather() {
        return weather;
    }

    public void setWeather(List<Map<String, Object>> weather) {
        this.weather = weather;
    }

    public String getName() {
        return name;
    }
}

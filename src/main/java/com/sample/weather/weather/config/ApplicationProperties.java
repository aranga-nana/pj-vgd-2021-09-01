package com.sample.weather.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application",ignoreUnknownFields = true)
public class ApplicationProperties {

    private Integer throttle;
    private String weatherMapApiKey;
    private Integer ttl;
    private String apiBaseUrl;

    public Integer getThrottle() {
        return throttle;
    }

    public void setThrottle(Integer throttle) {
        this.throttle = throttle;
    }

    public String getWeatherMapApiKey() {
        return weatherMapApiKey;
    }

    public void setWeatherMapApiKey(String weatherMapApiKey) {
        this.weatherMapApiKey = weatherMapApiKey;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "throttle=" + throttle +
                ", weatherMapApiKey='" + weatherMapApiKey + '\'' +
                ", ttl=" + ttl +
                ", apiBaseUrl='" + apiBaseUrl + '\'' +
                '}';
    }
}

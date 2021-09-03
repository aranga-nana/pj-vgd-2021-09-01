package com.sample.weather.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application",ignoreUnknownFields = true)
public class ApplicationProperties {

    public Integer getThrottle() {
        return throttle;
    }

    public void setThrottle(Integer throttle) {
        this.throttle = throttle;
    }

    private Integer throttle;



    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "throttle=" + throttle +
                '}';
    }
}

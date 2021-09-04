package com.sample.weather.weather.web;

import com.sample.weather.weather.common.ErrorCode;
import com.sample.weather.weather.common.OutputResult;
import com.sample.weather.weather.services.ApiKeyService;
import com.sample.weather.weather.services.WeatherService;
import com.sample.weather.weather.services.dto.WeatherInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WeatherController {

    private final WeatherService weatherService;
    private final ApiKeyService apiKeyService;

    public WeatherController(WeatherService weatherService, ApiKeyService apiKeyService) {
        this.weatherService = weatherService;
        this.apiKeyService = apiKeyService;
    }
    @GetMapping("/weather/current")
    public ResponseEntity<WeatherInfoDTO> weather(@RequestParam String country, @RequestParam String city, @RequestParam String apiKey) {

        if (StringUtils.isEmpty(country) || StringUtils.isEmpty(city)) {
            return ResponseEntity.badRequest().build();
        }
        final OutputResult valid = apiKeyService.validate(apiKey);
        if (!valid.isSuccess()) {
            if (ErrorCode.rateExceeded.equals(valid.getErrorCode())) {
                return ResponseEntity.status(429).build();
            }
            return ResponseEntity.status(403).build();
        }
        final OutputResult<WeatherInfoDTO> result = weatherService.getWeather(country,city);
        if (result.isSuccess()) {
            return ResponseEntity.ok().body(result.getData());
        }
        if (ErrorCode.notFound.equals(result.getErrorCode())) {
            return ResponseEntity.notFound().build();
        }
        if (ErrorCode.error.equals(result.getErrorCode())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(500).build();
    }
}

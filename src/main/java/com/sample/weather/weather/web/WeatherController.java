package com.sample.weather.weather.web;

import com.sample.weather.weather.common.ErrorCode;
import com.sample.weather.weather.common.OutputResult;
import com.sample.weather.weather.services.ApiKeyService;
import com.sample.weather.weather.services.WeatherService;
import com.sample.weather.weather.services.dto.ErrorResponse;
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
    public ResponseEntity<?> weather(@RequestParam(required = false) String country, @RequestParam(required = false) String city, @RequestParam(required = false) String apiKey) {

        if (StringUtils.isEmpty(country) || StringUtils.isEmpty(city)) {
            return ResponseEntity.badRequest().build();
        }
        final OutputResult valid = apiKeyService.validate(apiKey);
        if (!valid.isSuccess()) {
            if (ErrorCode.rateExceeded.equals(valid.getErrorCode())) {
                return ResponseEntity.status(429).body(new ErrorResponse(429,"rate exceeded","/api/weather/current"));
            }
            return ResponseEntity.status(403).body(new ErrorResponse(403,"forbidden","/api/weather/current"));

        }
        final OutputResult<WeatherInfoDTO> result = weatherService.getWeather(country,city);
        if (result.isSuccess()) {
            return ResponseEntity.ok().body(result.getData());
        }
        if (ErrorCode.notFound.equals(result.getErrorCode())) {
            return ResponseEntity.status(404).body(
                    new ErrorResponse(404,"country / city not found","/api/weather/current"));
        }
        if (ErrorCode.error.equals(result.getErrorCode())) {
            return ResponseEntity.status(400).body(
                    new ErrorResponse(400,"missing required parameters(country and city required)","/api/weather/current"));
        }
        return ResponseEntity.status(500).body(
                new ErrorResponse(500,"internal server error","/api/weather/current"));
    }
}

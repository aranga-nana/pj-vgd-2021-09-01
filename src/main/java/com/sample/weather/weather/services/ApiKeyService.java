package com.sample.weather.weather.services;

import com.sample.weather.weather.common.OutputResult;

public interface ApiKeyService {
    OutputResult validate(String apiKey);

}

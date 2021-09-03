package com.sample.weather.weather.services;

import com.sample.weather.weather.common.OutputResult;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {
    @Override
    public OutputResult validate(String apiKey) {
        return new OutputResult();
    }

    @Override
    public String generateNewKey() {
        return null;
    }
}

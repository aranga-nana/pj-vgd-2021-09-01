package com.sample.weather.weather.services;

import com.sample.weather.weather.common.OutputResult;

import java.util.Date;

public interface ApiKeyService {
    /**
     * validate key
     * @param apiKey
     * @return
     */
    OutputResult validate(String apiKey);

    /**
     * Generate new key based on email and expiry. 0 mean never expired
     * @param email
     * @param expiredIn
     * @param issueAt
     * @return
     */
    String generateNewKey(String email, int expiredIn, Date issueAt);

    /**
     * key schema validation
     * @param apiKey
     * @return
     */
    boolean validateApiKey(String apiKey);



}

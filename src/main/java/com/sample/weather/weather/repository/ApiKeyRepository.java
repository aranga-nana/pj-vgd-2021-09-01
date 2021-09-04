package com.sample.weather.weather.repository;

import com.sample.weather.weather.domain.ApiKey;

import java.util.Optional;

public interface ApiKeyRepository {

    Optional<ApiKey> findByKey(String key);

    boolean update(ApiKey key);
}

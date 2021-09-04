package com.sample.weather.weather.repository;

import com.sample.weather.weather.domain.ApiKey;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends CrudRepository<ApiKey, Long> {

    List<ApiKey> findAll();
    Optional<ApiKey> findById(Long id);
    Optional<ApiKey> findByKey(String key);

}

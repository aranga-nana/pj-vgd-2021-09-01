package com.sample.weather.weather.repository;

import com.sample.weather.weather.domain.Weather;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface WeatherRepository extends CrudRepository<Weather,Long> {

    Optional<Weather> findByCountryAndCity(String country, String city);
}

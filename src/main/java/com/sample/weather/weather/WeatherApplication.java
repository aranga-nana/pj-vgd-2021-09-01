package com.sample.weather.weather;

import com.sample.weather.weather.config.ApplicationProperties;
import com.sample.weather.weather.domain.ApiKey;
import com.sample.weather.weather.repository.ApiKeyRepository;
import com.sample.weather.weather.services.ApiKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class WeatherApplication implements CommandLineRunner {
	private static Logger logger = LoggerFactory.getLogger("weather-app");

	@Autowired
	private ApiKeyRepository repository;

	@Autowired
	private ApiKeyService apiKeyService;

	@Autowired
	private ApplicationProperties properties;

	public static void main(String[] args) {
		SpringApplication.run(WeatherApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		logger.info("{}",properties);
		logger.info("=============== Already created 5 never expire Keys =====================");
		int i =0;
		try {
			List<ApiKey> keys = repository.findAll();
			for(ApiKey key: keys) {
				i++;
				logger.info("KEY {} => {} ",i,key.getKey());
			}

		}catch (Throwable e){}
	}
}

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
		logger.info("=============== Generating 5 never expire Keys =====================");
		List<ApiKey> keys = repository.findAll();
		if (keys.size() == 0) {
			logger.info("CREATING NEW SET OF KEYS");
			for(int i=0;i < 5;i++) {
				ApiKey key = new ApiKey();
				key.setEmail("user"+i+"@example.com.au");
				String k = apiKeyService.generateNewKey(key.getEmail(),0,new Date());
				key.setKey(k);
				key.setUpdated(new Date());
				key.setInvocations(0);
				repository.save(key);
				logger.info("KEY {} => {} ",i,key.getKey());
			}
		}
		else {
			int i =0;

			for(ApiKey key: keys) {
				i++;
				logger.info("KEY {} => {} ",i,key.getKey());
			}
		}
	}
}

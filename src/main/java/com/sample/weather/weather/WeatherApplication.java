package com.sample.weather.weather;

import com.sample.weather.weather.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WeatherApplication implements CommandLineRunner {
	private static Logger logger = LoggerFactory.getLogger(WeatherApplication.class);
	@Autowired
	private ApplicationProperties properties;

	public static void main(String[] args) {
		SpringApplication.run(WeatherApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("{}",properties);
	}
}

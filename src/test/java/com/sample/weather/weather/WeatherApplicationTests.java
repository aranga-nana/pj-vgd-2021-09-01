package com.sample.weather.weather;

import com.sample.weather.weather.config.ApplicationProperties;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WeatherApplicationTests {

	@Autowired
	ApplicationProperties applicationProperties;

	@Test
	void contextLoadsAndConfig() {
		Assert.assertEquals(5,applicationProperties.getThrottle().intValue());
	}

}

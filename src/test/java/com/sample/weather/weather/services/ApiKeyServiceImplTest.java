package com.sample.weather.weather.services;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ApiKeyServiceImplTest {
    private static Logger logger = LoggerFactory.getLogger("api-key-service-test");
    @Autowired
    private ApiKeyService apiKeyService;

    @Test
    void validateThrottleFailed() {

    }

    @Test
    void generateNewKeyTest() {
        String apiKey = apiKeyService.generateNewKey("10001","abc@outlook.com.au");
        logger.info("{}",apiKey);
        Assert.assertNotNull(apiKey);
        String[] parts = apiKey.split("\\.");
        Assert.assertEquals(3,parts.length);


    }
}

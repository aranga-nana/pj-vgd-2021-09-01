package com.sample.weather.weather.services;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.any;


import com.sample.weather.weather.common.ErrorCode;
import com.sample.weather.weather.common.OutputResult;
import com.sample.weather.weather.domain.ApiKey;
import com.sample.weather.weather.repository.ApiKeyRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class ApiKeyServiceImplTest {
    private static Logger logger = LoggerFactory.getLogger("api-key-service-test");

    @MockBean
    private ApiKeyRepository repository;
    @Autowired
    private ApiKeyService apiKeyService;

    private String validApiKy;


    @Test
    void validationKeyNotFound() {
        validApiKy = apiKeyService.generateNewKey("aa@aa.com",0,new Date());
        doReturn(Optional.empty()).when(repository).findByKey(any());
        OutputResult valid = apiKeyService.validate(validApiKy);
        Assert.assertFalse(valid.isSuccess());
        Assert.assertEquals(ErrorCode.forbidden,valid.getErrorCode());
    }
    @Test
    void validationEmptyKey() {
        doReturn(Optional.empty()).when(repository).findByKey(null);
        OutputResult valid = apiKeyService.validate(null);
        Assert.assertFalse(valid.isSuccess());
        Assert.assertEquals(ErrorCode.forbidden,valid.getErrorCode());
    }

    @Test
    void validateThrottleSuccess() {
        validApiKy = apiKeyService.generateNewKey("aa3@aa.com",0,new Date());


        ApiKey mockKey = new ApiKey();
        mockKey.setInvocations(1);
        mockKey.setUpdated(new Date());
        mockKey.setKey("ZFhObGNqRkFkMlZoZEdobGNpMWxlR0Z0Y0d4bExtTnZiUzVoZFE9PS4xNjMwODk1ODc0MDQ5LjE2MzA4OTU4NzQwNDkuN0MzMUQ5NUQwOUI4OTY2QzhBMTMzRDZGQUYwOUYxMTk=");
        doReturn(Optional.of(mockKey)).when(repository).findByKey(any());

        OutputResult valid = apiKeyService.validate("ZFhObGNqRkFkMlZoZEdobGNpMWxlR0Z0Y0d4bExtTnZiUzVoZFE9PS4xNjMwODk1ODc0MDQ5LjE2MzA4OTU4NzQwNDkuN0MzMUQ5NUQwOUI4OTY2QzhBMTMzRDZGQUYwOUYxMTk=");
        Assert.assertTrue(valid.isSuccess());

    }

    @Test
    void validateThrottleRest() {
        // create key which 1 invocation done in 1 hour go throttle maxed;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,-4);
        calendar.add(Calendar.MINUTE,-15);
        logger.info("{}",calendar.getTime());
        ApiKey mockKey = new ApiKey();
        mockKey.setInvocations(5);
        mockKey.setUpdated(calendar.getTime());
        mockKey.setKey("ZFhObGNqRkFkMlZoZEdobGNpMWxlR0Z0Y0d4bExtTnZiUzVoZFE9PS4xNjMwODk1ODc0MDQ5LjE2MzA4OTU4NzQwNDkuN0MzMUQ5NUQwOUI4OTY2QzhBMTMzRDZGQUYwOUYxMTk=");
        doReturn(Optional.of(mockKey)).when(repository).findByKey(any());
        OutputResult valid = apiKeyService.validate("ZFhObGNqRkFkMlZoZEdobGNpMWxlR0Z0Y0d4bExtTnZiUzVoZFE9PS4xNjMwODk1ODc0MDQ5LjE2MzA4OTU4NzQwNDkuN0MzMUQ5NUQwOUI4OTY2QzhBMTMzRDZGQUYwOUYxMTk=");
        assertTrue(valid.isSuccess());

    }

    @Test
    void validateThrottleFailed() {
        validApiKy = apiKeyService.generateNewKey("aa@aa.com",0,new Date());

        ApiKey mockKey = new ApiKey();
        mockKey.setInvocations(5);
        mockKey.setUpdated(new Date());
        mockKey.setKey(validApiKy);
        doReturn(Optional.of(mockKey)).when(repository).findByKey(validApiKy);
        OutputResult valid = apiKeyService.validate(validApiKy);
        Assert.assertFalse(valid.isSuccess());
    }

    @Test
    void generateNewKeyTest() {
        String apiKey = apiKeyService.generateNewKey("user1@expal.com.au",30,new Date());
        logger.info("API Keys{}",apiKey);
        logger.info("{}",apiKey);
        String decode = new String(Base64.getDecoder().decode(apiKey));

        String[] parts = decode.split("\\.");
        Assert.assertEquals(4,parts.length);
    }
    @Test
    void ValidateKeySuccess() {
        String apiKey = apiKeyService.generateNewKey("user1@expal.com.au",30,new Date());
        assertTrue(apiKeyService.validateApiKey(apiKey));
    }
    @Test
    void ValidateKeyExpire() {
        // generate key  issue 1 hour a go and only valid for 5 min
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,-1);
        String apiKey = apiKeyService.generateNewKey("user1@expal.com.au",5,calendar.getTime());

        assertFalse(apiKeyService.validateApiKey(apiKey));
    }
    @Test
    void generate5keys() {
        for(int i=0;i < 5;i++){
            String user = "user"+i+"@weather-example.com.au";
            String apiKey = apiKeyService.generateNewKey(user,0,new Date());
            logger.info("KEY{} {} {}",i,user,apiKey);
        }
    }

}

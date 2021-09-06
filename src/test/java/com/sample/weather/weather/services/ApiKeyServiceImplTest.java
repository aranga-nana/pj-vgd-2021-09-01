package com.sample.weather.weather.services;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ApiKeyServiceImplTest {
    private static Logger logger = LoggerFactory.getLogger("api-key-service-test");

    @MockBean
    private ApiKeyRepository repository;
    @Autowired
    private ApiKeyService apiKeyService;

    private String validApiKy;

    @Before
    void init(){

    }

    @Test
    void validationKeyNotFound() {

        doReturn(Optional.empty()).when(repository).findByKey(any());
        OutputResult valid = apiKeyService.validate("10001.YWJjQG91dGxvb2suY29tLmF1.4B74922CC2D00E4A79D7181F247F82B7");
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
        validApiKy = apiKeyService.generateNewKey("aa@aa.com",0,new Date());

        // make  minute than current time => 1st invocation after reset
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,-55); // 55 minute beforen current time (1st invocation)

        ApiKey mockKey = new ApiKey();
        mockKey.setInvocations(4);
        mockKey.setUpdated(calendar.getTime());
        mockKey.setKey(validApiKy);
        doReturn(Optional.of(mockKey)).when(repository).findByKey(validApiKy);

        OutputResult valid = apiKeyService.validate(validApiKy);
        Assert.assertTrue(valid.isSuccess());

    }

    @Test
    void validateThrottleRest() {
        // create key which 1 invocation done in 1 hour go throttle maxed;
        validApiKy = apiKeyService.generateNewKey("aa@aa.com",0,new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,-1);
        calendar.add(Calendar.MINUTE,-2);
        logger.info("{}",calendar.getTime());
        ApiKey mockKey = new ApiKey();
        mockKey.setInvocations(5);
        mockKey.setUpdated(calendar.getTime());
        mockKey.setKey(validApiKy);
        doReturn(Optional.of(mockKey)).when(repository).findByKey(validApiKy);
        OutputResult valid = apiKeyService.validate(validApiKy);
        Assert.assertTrue(valid.isSuccess());

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

package com.sample.weather.weather.services;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sample.weather.weather.common.ErrorCode;
import com.sample.weather.weather.common.OutputResult;
import com.sample.weather.weather.domain.ApiKey;
import com.sample.weather.weather.repository.ApiKeyRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

        // make  minute than current time => 1st invocation after reset
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE,-55); // 55 minute beforen current time (1st invocation)

        ApiKey mockKey = new ApiKey();
        mockKey.setInvocations(4);
        mockKey.setUpdated(calendar.getTime());
        mockKey.setKey("10001.YWJjQG91dGxvb2suY29tLmF1.4B74922CC2D00E4A79D7181F247F82B7");
        doReturn(Optional.of(mockKey)).when(repository).findByKey(any());

        OutputResult valid = apiKeyService.validate("10001.YWJjQG91dGxvb2suY29tLmF1.4B74922CC2D00E4A79D7181F247F82B7");
        Assert.assertTrue(valid.isSuccess());

    }


    @Test
    void validateThrottleFailed() {

        ApiKey mockKey = new ApiKey();
        mockKey.setInvocations(5);
        mockKey.setUpdated(new Date());
        mockKey.setKey("10001.YWJjQG91dGxvb2suY29tLmF1.4B74922CC2D00E4A79D7181F247F82B7");
        doReturn(Optional.of(mockKey)).when(repository).findByKey(any());
        OutputResult valid = apiKeyService.validate("10001.YWJjQG91dGxvb2suY29tLmF1.4B74922CC2D00E4A79D7181F247F82B7");
        Assert.assertFalse(valid.isSuccess());
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

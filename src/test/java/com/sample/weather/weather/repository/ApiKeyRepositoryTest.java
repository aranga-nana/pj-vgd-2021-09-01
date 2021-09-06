package com.sample.weather.weather.repository;

import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.sample.weather.weather.domain.ApiKey;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith({DBUnitExtension.class, SpringExtension.class})
@SpringBootTest
@ActiveProfiles("test")
class ApiKeyRepositoryTest {



    @Autowired
    private DataSource dataSource;

    @Autowired
    private ApiKeyRepository repository;

    public ConnectionHolder getConnectionHolder() {
        // Return a function that retrieves a connection from our data source
        return () -> dataSource.getConnection();
    }
    @Test
    @DataSet("keys.yml")
    void findAll() {
        List<ApiKey> apiKeys = repository.findAll();

        Assert.assertNotNull(apiKeys);
        Assert.assertEquals(5,apiKeys.size());

    }

    @Test
    @DataSet("keys.yml")
    void findByKey() {
        Optional<ApiKey> apiKeys = repository.findByKey("ZFhObGNqQkFkMlZoZEdobGNpMWxlR0Z0Y0d4bExtTnZiUzVoZFE9PS4xNjMwODk1ODc0MDQ4LjE2MzA4OTU4NzQwNDguNTIyMUJCQTQyNTU2RTcwNkQ5MEJGNTQ2NUE3M0JEMzQ=");
        Assert.assertTrue(apiKeys.isPresent());
    }
    @Test
    @DataSet("keys.yml")
    void findByKeyUnsuccessful() {
        Optional<ApiKey> apiKeys = repository.findByKey("101.qwewqeqw.1");
        Assert.assertFalse(apiKeys.isPresent());
    }

    @Test
    @DataSet(value = "keys.yml")
    void save() {
        ApiKey apiKey = new ApiKey();
        apiKey.setUpdated(new Date());
        apiKey.setInvocations(1);
        apiKey.setId(Long.valueOf(150));
        apiKey.setKey("1010.aisduaosduosudasoasdi");
        ApiKey savedApiKey = repository.save(apiKey);
        Assert.assertNotNull(savedApiKey);

    }
}

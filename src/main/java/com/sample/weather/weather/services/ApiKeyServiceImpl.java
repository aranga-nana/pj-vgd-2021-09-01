package com.sample.weather.weather.services;

import com.sample.weather.weather.common.ErrorCode;
import com.sample.weather.weather.common.OutputResult;
import com.sample.weather.weather.config.ApplicationProperties;
import com.sample.weather.weather.domain.ApiKey;
import com.sample.weather.weather.repository.ApiKeyRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    private final static OutputResult FORBIDDEN_STATUS
            = new OutputResult().withSuccess(false).withErrorCode(ErrorCode.forbidden);

    private final ApiKeyRepository repository;
    private final ApplicationProperties appProp;

    public ApiKeyServiceImpl(ApiKeyRepository repository, ApplicationProperties appProp) {
        this.repository = repository;
        this.appProp = appProp;
    }

    @Override
    public OutputResult validate(String apiKey) {
        if (StringUtils.isEmpty(apiKey)) {
            return FORBIDDEN_STATUS;
        }
        Optional<ApiKey> oKey = repository.findByKey(apiKey);
        if (!oKey.isPresent()){
            return FORBIDDEN_STATUS;
        }
        final ApiKey key = oKey.get();
        key.setInvocations(key.getInvocations()+ 1);
        // date operation
        Instant from = key.getUpdated().toInstant();
        Instant now = Instant.now();
        Duration duration = Duration.between(from,now);
        if (duration.toMinutes() < 60 && key.getInvocations() > 5) {

            return new OutputResult().withSuccess(false).withErrorCode(ErrorCode.rateExceeded);
        }
        // reset the updated date and counter when 1 hour passed last invocation
        if (duration.toMinutes() > 60){
           key.setInvocations(1);
           key.setUpdated(new Date());
        }
        repository.save(key);
        return new OutputResult().withSuccess(true);
    }

    /**
     * generate key based on
     * NOTE key in the format <appid>.<base 64 owner email>.<md5 check some of the 2 parts>
     * @param appId
     * @param appOwnerEmail
     * @return
     */
    @Override
    public String generateNewKey(String appId,String appOwnerEmail) {
        String encodeEmail = Base64.getEncoder().encodeToString(appOwnerEmail.getBytes());
        String payload = appId+"."+encodeEmail;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(payload.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter
                    .printHexBinary(digest);
            return payload+"."+myHash;
        } catch (NoSuchAlgorithmException e) {

        }
        return null;
    }


}

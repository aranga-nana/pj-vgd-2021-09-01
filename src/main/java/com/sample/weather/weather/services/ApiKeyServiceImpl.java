package com.sample.weather.weather.services;

import com.sample.weather.weather.common.ErrorCode;
import com.sample.weather.weather.common.OutputResult;
import com.sample.weather.weather.config.ApplicationProperties;
import com.sample.weather.weather.domain.ApiKey;
import com.sample.weather.weather.repository.ApiKeyRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    private static Logger logger = LoggerFactory.getLogger(ApiKeyServiceImpl.class);

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
        if (!validateApiKey(apiKey)) {
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
        if (duration.toMinutes() <= 60 && key.getInvocations() > 5) {

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
    @Override
    public boolean validateApiKey(String apiKey){
       if (StringUtils.isEmpty(apiKey)){
           return false;
       }
       Date now = new Date();
       try {
           byte[] decode =  Base64.getDecoder().decode(apiKey);
           String decodedKey = new String(decode);
           String parts[] = decodedKey.split("\\.");
           if (parts.length != 4) {
               logger.error("Invalid api key. encode error");
               return false;
           }
           if (!NumberUtils.isCreatable(parts[2])) {
               logger.error("Invalid api key. cannot find expireAt");
               return false;
           }
           // expiry check
           Long expireAt = Long.valueOf(parts[2]);
           if (expireAt.longValue() != 0 && now.getTime() > expireAt){
               logger.error("api token expired . expiredAt {}",expireAt);
               return false;
           }
           logger.info("Never expire key(true|false): {}",expireAt.longValue() == 0 ? true: false);
           // tamper check
           String payload = parts[0]+"."+parts[1]+"."+parts[2];
           String hash = generateHashWithSalt(payload);
           if (!hash.equals(parts[3])) {
               logger.error("token has not matched. token is tampered with original hash {}",parts[3]);
               return false;
           }
           return true;

       }catch (Throwable e){
           logger.error("critical error while processing token, {}",e.getMessage(),e);
           return false;
       }

    }
    /**
     * generate key based on schema
     *
     * @param email
     * @param expiredIn minutes | 0 non expire keys
     * @Parma issueAt issue time
     * @return
     */
    @Override
    public String generateNewKey(String email,int expiredIn,Date issueAt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(issueAt);
        calendar.add(Calendar.MINUTE,expiredIn);
        long expireAt = 0;
        if (expiredIn != 0){
            expireAt = calendar.getTime().getTime();
        }

        String payLoad = new String(Base64.getEncoder().encode(email.getBytes()))+"."+issueAt.getTime()+"."+expireAt;
        String hash = generateHashWithSalt(payLoad);
        return Base64.getEncoder().encodeToString((payLoad+"."+hash).getBytes());
    }
    private  String generateHashWithSalt(String payLoad){
        try {
            payLoad = payLoad + appProp.getSalt();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(payLoad.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter
                    .printHexBinary(digest);
            return myHash;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }


}

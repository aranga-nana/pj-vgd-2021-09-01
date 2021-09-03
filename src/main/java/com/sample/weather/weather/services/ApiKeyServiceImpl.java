package com.sample.weather.weather.services;

import com.sample.weather.weather.common.OutputResult;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {
    @Override
    public OutputResult validate(String apiKey) {
        return new OutputResult();
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

        return null;
    }
}

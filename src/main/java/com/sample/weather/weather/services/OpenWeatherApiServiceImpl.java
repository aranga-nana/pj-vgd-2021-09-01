package com.sample.weather.weather.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sample.weather.weather.config.ApplicationProperties;
import com.sample.weather.weather.services.dto.OpenWeatherMapResponse;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenWeatherApiServiceImpl implements OpenWeatherApiService {
    private static Logger logger = LoggerFactory.getLogger(OpenWeatherApiServiceImpl.class);

    private final ApplicationProperties appProp;
    private final ObjectMapper mapper = new ObjectMapper();
    private final static List<String> COUNTRY_CODE = Arrays.asList(Locale.getISOCountries());


    public OpenWeatherApiServiceImpl(ApplicationProperties appProp) {
        this.appProp = appProp;
    }

    @Override
    public Optional<String> getWeatherDescription(String country, String city) {
        if (StringUtils.isEmpty(city) || StringUtils.isEmpty(country)) {
            return Optional.empty();
        }

        RestTemplate template = new RestTemplate();
        StringBuilder reqBuilder = new StringBuilder(appProp.getApiBaseUrl());
        reqBuilder.append("?");
        reqBuilder.append("q=");
        reqBuilder.append(city);
        reqBuilder.append(",");
        reqBuilder.append(country);
        reqBuilder.append("&");
        reqBuilder.append("appid=");
        reqBuilder.append(appProp.getWeatherMapApiKey());

        try {

            ResponseEntity<OpenWeatherMapResponse> response =  template.getForEntity(reqBuilder.toString(),
                    OpenWeatherMapResponse.class);
            if (response.getStatusCodeValue() == 200
                    && response.getBody() != null
                    && response.getBody().getWeather().size() > 0) {

                return Optional.of((String)response.getBody().getWeather().get(0).get("description"));
            }
        }catch (HttpClientErrorException ex) {
              // capture 404 errors
              if (ex.getRawStatusCode() == 404) {
                  return Optional.empty();
              }
              logger.error("open weather critical error error code {}", ex.getRawStatusCode(),ex);
              throw ex;
        }
       return Optional.empty();
    }
}

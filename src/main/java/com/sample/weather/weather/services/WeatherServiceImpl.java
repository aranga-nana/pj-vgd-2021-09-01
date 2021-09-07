package com.sample.weather.weather.services;

import com.sample.weather.weather.common.ErrorCode;
import com.sample.weather.weather.common.OutputResult;
import com.sample.weather.weather.config.ApplicationProperties;
import com.sample.weather.weather.domain.Weather;
import com.sample.weather.weather.repository.WeatherRepository;
import com.sample.weather.weather.services.dto.WeatherInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;


@Service
@Transactional
public class WeatherServiceImpl implements WeatherService {

    private static Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);

    private final WeatherRepository weatherRepository;
    private final OpenWeatherApiService apiService;
    private final ApplicationProperties appProp;

    public WeatherServiceImpl(WeatherRepository weatherRepository, OpenWeatherApiService apiService, ApplicationProperties appProp) {
        this.weatherRepository = weatherRepository;
        this.apiService = apiService;
        this.appProp = appProp;
    }

    @Override
    public OutputResult<WeatherInfoDTO> getWeather(String country, String city) {

        if (StringUtils.isEmpty(city) || StringUtils.isEmpty(city)) {
            return new OutputResult<WeatherInfoDTO>().withSuccess(false).withErrorCode(ErrorCode.error);
        }
        Optional<Weather> oWeather = weatherRepository.findByCountryAndCity(country,city);
        Weather weather = new Weather();
        weather.setCity(city);
        weather.setCountry(country);

        final WeatherInfoDTO infoDTO = new WeatherInfoDTO();
        infoDTO.setCountry(country);
        infoDTO.setCity(city);

        if (oWeather.isPresent()) {
            weather = oWeather.get();
            Instant from = oWeather.get().getUpdated().toInstant();
            Instant now = Instant.now();
            Duration duration = Duration.between(from,now);
            final long ttlInMinutes = appProp.getTtl()* 60;

            if (duration.toMinutes() <= ttlInMinutes ) {
                infoDTO.setDescription(oWeather.get().getDescription());
                return new OutputResult<WeatherInfoDTO>().withData(infoDTO).withSuccess(true);
            }
            logger.info("TTL of weather report expired. request new data from the open weather . TTL set to {} hours",appProp.getTtl());
        }

        try {
            Optional<String> oDescription = apiService.getWeatherDescription(country,city);
            if (oDescription.isPresent()){
                infoDTO.setDescription(oDescription.get());
                weather.setDescription(infoDTO.getDescription());
                weather.setUpdated(new Date());
                Weather saved = weatherRepository.save(weather);
                logger.info("UPDATED/CREATED RECORD {}",saved.getId());
                return new OutputResult<WeatherInfoDTO>().withData(infoDTO).withSuccess(true);
            }
            return new OutputResult<WeatherInfoDTO>().withSuccess(false).withErrorCode(ErrorCode.notFound);
        }catch (Throwable e){
            logger.error("API ERROR",e);
            return new OutputResult<WeatherInfoDTO>().withSuccess(false).withErrorCode(ErrorCode.internalServiceError);
        }

    }
}

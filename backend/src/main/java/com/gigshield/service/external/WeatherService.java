package com.gigshield.service.external;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${gigshield.api.weatherbit.key}")
    private String apiKey;

    @Value("${gigshield.api.weatherbit.base-url}")
    private String baseUrl;

    @Value("${gigshield.api.mock-mode:false}")
    private boolean mockMode;

    public WeatherData getCurrentWeather(String city) {
        if (mockMode || "MOCK".equals(apiKey)) {
            return getMockWeather(city);
        }
        try {
            String url = baseUrl + "/current?city=" + city + ",IN&key=" + apiKey;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return parseWeatherbitResponse(response, city);
        } catch (RestClientException e) {
            log.warn("Weatherbit API call failed for city={}, falling back to mock.", city);
            return getMockWeather(city);
        }
    }

    public WeatherForecast getForecast(String city) {
        if (mockMode || "MOCK".equals(apiKey)) {
            return getMockForecast(city);
        }
        try {
            String url = baseUrl + "/forecast/daily?city=" + city + ",IN&days=5&key=" + apiKey;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return parseForecastResponse(response, city);
        } catch (RestClientException e) {
            log.warn("Weatherbit Forecast API call failed for city={}, falling back to mock.", city);
            return getMockForecast(city);
        }
    }

    @SuppressWarnings("unchecked")
    private WeatherData parseWeatherbitResponse(Map<String, Object> resp, String city) {
        if (resp == null) return getMockWeather(city);

        var dataArray = (java.util.List<Map<String, Object>>) resp.getOrDefault("data", java.util.List.of());
        if (dataArray.isEmpty()) return getMockWeather(city);

        Map<String, Object> current = dataArray.get(0);

        double temp = ((Number) current.getOrDefault("temp", 30.0)).doubleValue();
        double rain = ((Number) current.getOrDefault("precip", 0.0)).doubleValue();
        double humidity = ((Number) current.getOrDefault("rh", 50.0)).doubleValue();
        double clouds = ((Number) current.getOrDefault("clouds", 10.0)).doubleValue();
        double uv = ((Number) current.getOrDefault("uv", 5.0)).doubleValue();
        double precipAccum = ((Number) current.getOrDefault("precip", 0.0)).doubleValue();

        return WeatherData.builder()
                .city(city)
                .temperatureCelsius(temp)
                .rainfallMmPerHour(rain)
                .humidity(humidity)
                .cloudCover(clouds)
                .uvIndex(uv)
                .precipitation(precipAccum)
                .cycloneAlerted(false)
                .build();
    }

    @SuppressWarnings("unchecked")
    private WeatherForecast parseForecastResponse(Map<String, Object> resp, String city) {
        if (resp == null) return getMockForecast(city);

        var dataArray = (java.util.List<Map<String, Object>>) resp.getOrDefault("data", java.util.List.of());
        double maxTemp = 0, totalRain = 0;
        for (var entry : dataArray) {
            maxTemp = Math.max(maxTemp, ((Number) entry.getOrDefault("temp", 0.0)).doubleValue());
            totalRain += ((Number) entry.getOrDefault("precip", 0.0)).doubleValue();
        }
        return WeatherForecast.builder()
                .city(city)
                .weekAvgTempCelsius(maxTemp)
                .weekTotalRainfallMm(totalRain)
                .build();
    }

    private WeatherData getMockWeather(String city) {
        if (city == null) city = "Generic";
        return switch (city.toLowerCase()) {
            case "bangalore"  -> WeatherData.builder().city(city).temperatureCelsius(27.0).rainfallMmPerHour(15.5).humidity(65).cloudCover(20).uvIndex(7).precipitation(10).build();
            case "delhi"      -> WeatherData.builder().city(city).temperatureCelsius(39.0).rainfallMmPerHour(0.0).humidity(15).cloudCover(5).uvIndex(11).precipitation(0).build();
            case "mumbai"     -> WeatherData.builder().city(city).temperatureCelsius(31.0).rainfallMmPerHour(42.0).humidity(85).cloudCover(90).uvIndex(3).precipitation(50).build();
            default           -> WeatherData.builder().city(city).temperatureCelsius(32.0).rainfallMmPerHour(8.0).humidity(50).cloudCover(30).uvIndex(5).precipitation(5).build();
        };
    }

    private WeatherForecast getMockForecast(String city) {
        if (city == null) city = "Generic";
        return switch (city.toLowerCase()) {
            case "mumbai"  -> WeatherForecast.builder().city(city).weekAvgTempCelsius(31.0).weekTotalRainfallMm(120.0).build();
            default        -> WeatherForecast.builder().city(city).weekAvgTempCelsius(33.0).weekTotalRainfallMm(20.0).build();
        };
    }

    @Data
    @lombok.Builder
    public static class WeatherData {
        private String city;
        private double temperatureCelsius;
        private double rainfallMmPerHour;
        private double humidity;
        private double cloudCover;
        private double uvIndex;
        private double precipitation;
        private boolean cycloneAlerted;
        private String imdAlertLevel;
    }

    @Data
    @lombok.Builder
    public static class WeatherForecast {
        private String city;
        private double weekAvgTempCelsius;
        private double weekTotalRainfallMm;
    }
}

package com.gigshield.service.external;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);
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

        return new WeatherData(city, temp, rain, humidity, clouds, uv, precipAccum, false);
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
        return new WeatherForecast(city, maxTemp, totalRain);
    }

    private WeatherData getMockWeather(String city) {
        if (city == null) city = "Generic";
        return switch (city.toLowerCase()) {
            case "bangalore"  -> new WeatherData(city, 27.0, 15.5, 65, 20, 7, 10, false);
            case "delhi"      -> new WeatherData(city, 39.0, 0.0, 15, 5, 11, 0, false);
            case "mumbai"     -> new WeatherData(city, 31.0, 42.0, 85, 90, 3, 50, false);
            default           -> new WeatherData(city, 32.0, 8.0, 50, 30, 5, 5, false);
        };
    }

    private WeatherForecast getMockForecast(String city) {
        if (city == null) city = "Generic";
        return switch (city.toLowerCase()) {
            case "mumbai"  -> new WeatherForecast(city, 31.0, 120.0);
            default        -> new WeatherForecast(city, 33.0, 20.0);
        };
    }

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

        public WeatherData(String city, double temperatureCelsius, double rainfallMmPerHour, 
                           double humidity, double cloudCover, double uvIndex, 
                           double precipitation, boolean cycloneAlerted) {
            this.city = city;
            this.temperatureCelsius = temperatureCelsius;
            this.rainfallMmPerHour = rainfallMmPerHour;
            this.humidity = humidity;
            this.cloudCover = cloudCover;
            this.uvIndex = uvIndex;
            this.precipitation = precipitation;
            this.cycloneAlerted = cycloneAlerted;
        }

        public String getCity() { return city; }
        public double getTemperatureCelsius() { return temperatureCelsius; }
        public double getRainfallMmPerHour() { return rainfallMmPerHour; }
        public double getHumidity() { return humidity; }
        public double getCloudCover() { return cloudCover; }
        public double getUvIndex() { return uvIndex; }
        public double getPrecipitation() { return precipitation; }
        public boolean isCycloneAlerted() { return cycloneAlerted; }
        public String getImdAlertLevel() { return imdAlertLevel; }
    }

    public static class WeatherForecast {
        private String city;
        private double weekAvgTempCelsius;
        private double weekTotalRainfallMm;

        public WeatherForecast(String city, double weekAvgTempCelsius, double weekTotalRainfallMm) {
            this.city = city;
            this.weekAvgTempCelsius = weekAvgTempCelsius;
            this.weekTotalRainfallMm = weekTotalRainfallMm;
        }

        public double getWeekAvgTempCelsius() { return weekAvgTempCelsius; }
        public double getWeekTotalRainfallMm() { return weekTotalRainfallMm; }
    }
}

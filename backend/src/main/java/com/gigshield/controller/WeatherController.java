package com.gigshield.controller;

import com.gigshield.service.external.AQIService;
import com.gigshield.service.external.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final AQIService aqiService;

    /**
     * Gets the live weather and AQI for a city to power the Weather Alerts dashboard.
     */
    @GetMapping("/monitor/{city}")
    public ResponseEntity<?> getMonitorData(@PathVariable String city) {
        WeatherService.WeatherData weather = weatherService.getCurrentWeather(city);
        AQIService.AQIData aqi = aqiService.getCurrentAQI(city, city); // Use city as state for simplicity

        return ResponseEntity.ok(Map.of(
            "city", city,
            "temperature", weather.getTemperatureCelsius(),
            "rainfall", weather.getRainfallMmPerHour(),
            "aqi", aqi.getCurrentAqi(),
            "cycloneAlert", weather.isCycloneAlerted(),
            "status", "FETCHED_LIVE"
        ));
    }

    @GetMapping("/current/{city}")
    public ResponseEntity<WeatherService.WeatherData> getCurrentWeather(@PathVariable String city) {
        return ResponseEntity.ok(weatherService.getCurrentWeather(city));
    }

    @GetMapping("/aqi/{city}")
    public ResponseEntity<AQIService.AQIData> getAQI(@PathVariable String city) {
        return ResponseEntity.ok(aqiService.getCurrentAQI(city, city));
    }
}

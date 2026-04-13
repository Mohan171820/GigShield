package com.gigshield.service;

import com.gigshield.dto.FeatureRequestDTO;
import com.gigshield.entity.Worker;
import com.gigshield.repository.WorkerRepository;
import com.gigshield.service.external.AQIService;
import com.gigshield.service.external.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.gigshield.entity.ZoneMetrics;
import com.gigshield.repository.ZoneMetricsRepository;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MLDataService {

    private final WorkerRepository workerRepository;
    private final WeatherService weatherService;
    private final AQIService aqiService;
    private final ZoneMetricsRepository zoneMetricsRepository;

    public FeatureRequestDTO aggregateFeaturesForWorker(Long workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found: " + workerId));

        // 1. Fetch Live Weather & AQI (The 7 Remote Features)
        WeatherService.WeatherData weather = weatherService.getCurrentWeather(worker.getCity());
        AQIService.AQIData aqi = aqiService.getCurrentAQI(worker.getCity(), worker.getCity());

        // 2. Fetch Zone Aggregate Metrics
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        List<ZoneMetrics> recentMetrics = zoneMetricsRepository.findRecentMetrics(worker.getCity(), worker.getZone(), sevenDaysAgo);
        
        double avgZoneRain = 0.0;
        double maxZoneTemp = weather.getTemperatureCelsius(); // Fallback to current
        double avgZoneAqi  = aqi.getCurrentAqi();             // Fallback to current
        
        if (!recentMetrics.isEmpty()) {
            avgZoneRain = recentMetrics.stream().mapToDouble(ZoneMetrics::getAvgRainfallMm).average().orElse(0.0);
            maxZoneTemp = recentMetrics.stream().mapToDouble(ZoneMetrics::getMaxTemperatureCelsius).max().orElse(maxZoneTemp);
            avgZoneAqi  = recentMetrics.stream().mapToDouble(ZoneMetrics::getAvgAqi).average().orElse(avgZoneAqi);
        }

        // 3. Device-Level Logic (Mocking GPS Spoofing Detection)
        // If the worker's name implies testing or spoofing, flag them. Or 5% random chance for demo.
        boolean spoofed = worker.getName().toLowerCase().contains("spoof") || Math.random() < 0.05;

        return FeatureRequestDTO.builder()
                .temperature(weather.getTemperatureCelsius())
                .humidity(weather.getHumidity())
                .rain_mm(weather.getRainfallMmPerHour())
                .precipitation_mm(weather.getPrecipitation())
                .aqi(aqi.getCurrentAqi())
                .uv_index(weather.getUvIndex())
                .cloud_cover(weather.getCloudCover())
                .zone_avg_rain_mm(avgZoneRain)
                .zone_max_temp(maxZoneTemp)
                .zone_avg_aqi(avgZoneAqi)
                .gps_spoofed(spoofed)
                .build();
    }
}

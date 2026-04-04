package com.gigshield.service;

import com.gigshield.dto.FeatureRequestDTO;
import com.gigshield.entity.Worker;
import com.gigshield.repository.WorkerRepository;
import com.gigshield.service.external.AQIService;
import com.gigshield.service.external.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MLDataService {

    private final WorkerRepository workerRepository;
    private final WeatherService weatherService;
    private final AQIService aqiService;

    public FeatureRequestDTO aggregateFeaturesForWorker(Long workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found: " + workerId));

        // 1. Fetch Live Weather & AQI (The 7 Remote Features)
        WeatherService.WeatherData weather = weatherService.getCurrentWeather(worker.getCity());
        AQIService.AQIData aqi = aqiService.getCurrentAQI(worker.getCity(), worker.getCity());

        return FeatureRequestDTO.builder()
                .temperature(weather.getTemperatureCelsius())
                .humidity(weather.getHumidity())
                .rain_mm(weather.getRainfallMmPerHour())
                .precipitation_mm(weather.getPrecipitation())
                .aqi(aqi.getCurrentAqi())
                .uv_index(weather.getUvIndex())
                .cloud_cover(weather.getCloudCover())
                .build();
    }
}

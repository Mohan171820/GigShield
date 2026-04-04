package com.gigshield.service;

import com.gigshield.entity.ZoneMetrics;
import com.gigshield.repository.ZoneMetricsRepository;
import com.gigshield.service.external.AQIService;
import com.gigshield.service.external.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Nightly scheduler that pulls live weather and AQI data for all
 * supported cities/zones and persists them as ZoneMetrics rows.
 *
 * These records feed into the XGBoost feature aggregation:
 *   - zone_disruption_freq_90d
 *   - zone_avg_rainfall_mm
 *   - zone_avg_winter_aqi
 */
@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class ZoneMetricsScheduler {

    private final ZoneMetricsRepository zoneMetricsRepository;
    private final WeatherService weatherService;
    private final AQIService aqiService;

    /**
     * Supported city → (state, list of zones) mapping.
     * Add new cities/zones here as GigShield expands.
     */
    private static final Map<String, CityConfig> CITY_ZONE_MAP = Map.of(
        "Bangalore", new CityConfig("Karnataka",  List.of("Koramangala", "HSR Layout", "Whitefield")),
        "Delhi",     new CityConfig("Delhi",       List.of("Dwarka", "Connaught Place", "Lajpat Nagar")),
        "Mumbai",    new CityConfig("Maharashtra", List.of("Andheri West", "Dadar", "Bandra"))
    );

    /**
     * Runs every night at 01:00 AM IST.
     * Fetches weather + AQI and stores one ZoneMetrics row per zone.
     */
    @Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Kolkata")
    public void collectNightlyZoneMetrics() {
        LocalDate today = LocalDate.now();
        log.info("ZoneMetricsScheduler: Starting nightly collection for {}", today);

        for (var entry : CITY_ZONE_MAP.entrySet()) {
            String city = entry.getKey();
            CityConfig config = entry.getValue();

            // Fetch shared city-level data (one API call per city, then assigned to all zones)
            WeatherService.WeatherData weather = weatherService.getCurrentWeather(city);
            AQIService.AQIData aqi = aqiService.getCurrentAQI(city, config.state());

            for (String zone : config.zones()) {
                try {
                    ZoneMetrics metrics = buildZoneMetrics(city, zone, today, weather, aqi);
                    zoneMetricsRepository.save(metrics);
                    log.debug("Saved ZoneMetrics for city={} zone={} date={}", city, zone, today);
                } catch (Exception e) {
                    log.error("Failed to save ZoneMetrics for city={} zone={}: {}", city, zone, e.getMessage());
                }
            }
        }
        log.info("ZoneMetricsScheduler: Nightly collection complete.");
    }

    private ZoneMetrics buildZoneMetrics(
            String city, String zone, LocalDate date,
            WeatherService.WeatherData weather, AQIService.AQIData aqi) {

        ZoneMetrics m = new ZoneMetrics();
        m.setCity(city);
        m.setZone(zone);
        m.setDate(date);

        // Weather
        m.setMaxRainfallMm(weather.getRainfallMmPerHour());
        m.setAvgRainfallMm(weather.getRainfallMmPerHour() * 0.75); // Simple estimate
        m.setMaxTemperatureCelsius(weather.getTemperatureCelsius());
        m.setCycloneAlertIssued(weather.isCycloneAlerted());
        m.setImdAlertLevel(weather.getImdAlertLevel());

        // AQI
        m.setAvgAqi((double) aqi.getAvgAqi());
        m.setMaxAqi((double) aqi.getCurrentAqi());

        // Disruption flags
        boolean rainDisruption   = weather.getRainfallMmPerHour() > 35.0;
        boolean aqiDisruption    = aqi.getCurrentAqi() > 350;
        boolean heatDisruption   = weather.getTemperatureCelsius() > 44.0;
        boolean cycloneDisruption = weather.isCycloneAlerted();

        m.setCurfewDeclared(false);         // Requires manual / NDMA API feed
        m.setPlatformOutageDetected(false); // Requires Platform mock API
        m.setDisruptionEventOccurred(rainDisruption || aqiDisruption || heatDisruption || cycloneDisruption);

        return m;
    }

    /** Simple typed record for city configuration. */
    private record CityConfig(String state, List<String> zones) {}
}

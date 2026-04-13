package com.gigshield.service.external;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Fetches real-time AQI data from IQAir API.
 * Falls back to deterministic mock data when mock-mode is true or API key is absent.
 *
 * AQI scale used: CPCB India standard (same as US AQI for our purposes).
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AQIService {

    private static final Logger log = LoggerFactory.getLogger(AQIService.class);
    private final RestTemplate restTemplate;

    @Value("${gigshield.api.iqair.key}")
    private String apiKey;

    @Value("${gigshield.api.iqair.base-url}")
    private String baseUrl;

    @Value("${gigshield.api.mock-mode:true}")
    private boolean mockMode;

    /**
     * Returns the current AQI data for a given city.
     * @param city  City name ( "Delhi")
     * @param state State name ( "Delhi") — required by IQAir
     * @return      AQIData object with current and average AQI.
     */
    public AQIData getCurrentAQI(String city, String state) {
        if (mockMode || "MOCK".equals(apiKey)) {
            log.debug("Mock mode: returning synthetic AQI for {}", city);
            return getMockAQI(city);
        }
        try {
            String url = String.format(
                "%s/city?city=%s&state=%s&country=India&key=%s",
                baseUrl, city, state, apiKey
            );
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return parseIQAirResponse(response, city);
        } catch (RestClientException e) {
            log.warn("IQAir API call failed for city={}, falling back to mock. Error: {}", city, e.getMessage());
            return getMockAQI(city);
        }
    }


    @SuppressWarnings("unchecked")
    private AQIData parseIQAirResponse(Map<String, Object> resp, String city) {
        if (resp == null) return getMockAQI(city);
        try {
            Map<String, Object> data       = (Map<String, Object>) resp.get("data");
            Map<String, Object> current    = (Map<String, Object>) data.get("current");
            Map<String, Object> pollution  = (Map<String, Object>) current.get("pollution");

        int aqius = ((Number) pollution.get("aqius")).intValue();
        return new AQIData(city, aqius, aqius, categorizeAQI(aqius));
    } catch (Exception e) {
        log.error("Failed to parse IQAir response: {}", e.getMessage());
        return getMockAQI(city);
    }
}


private AQIData getMockAQI(String city) {
    if (city == null) city = "Generic";
    int aqi = switch (city.toLowerCase()) {
        case "delhi"     -> 320;  
        case "mumbai"    -> 150;  
        case "bangalore" -> 90;   
        case "chennai"   -> 110;  
        case "hyderabad" -> 130;  
        case "kolkata"   -> 200;  
        default          -> 140;
    };
    return new AQIData(city, aqi, aqi, categorizeAQI(aqi));
}

    private String categorizeAQI(int aqi) {
        if (aqi <= 50)  return "GOOD";
        if (aqi <= 100) return "SATISFACTORY";
        if (aqi <= 200) return "MODERATE";
        if (aqi <= 300) return "POOR";
        if (aqi <= 400) return "VERY_POOR";
        return "SEVERE";
    }

    public static class AQIData {
        private String city;
        private int currentAqi;
        private int avgAqi;   // Historical/seasonal average from ZoneMetrics
        private String category;

        public AQIData(String city, int currentAqi, int avgAqi, String category) {
            this.city = city;
            this.currentAqi = currentAqi;
            this.avgAqi = avgAqi;
            this.category = category;
        }

        public int getCurrentAqi() { return currentAqi; }
        public int getAvgAqi() { return avgAqi; }
    }
}

package com.gigshield.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the 7 features required 
 * by the Remote FastAPI risk scoring model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureRequestDTO {

    // 1. Current Temperature (Celsius)
    private double temperature;

    // 2. Relative Humidity (%)
    private double humidity;

    // 3. Current Rainfall (mm/hr)
    private double rain_mm;

    // 4. Accumulated Precipitation (mm)
    private double precipitation_mm;

    // 5. Air Quality Index (AQI)
    private int aqi;

    // 6. UV Index
    private double uv_index;

    // 7. Cloud Cover (%)
    private double cloud_cover;

    /**
     * Converts to double array for legacy compatibility.
     */
    public double[] toFeatureArray() {
        return new double[]{
            temperature, humidity, rain_mm, precipitation_mm, (double) aqi, uv_index, cloud_cover
        };
    }
}

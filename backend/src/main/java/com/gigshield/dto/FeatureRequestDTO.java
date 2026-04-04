package com.gigshield.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureRequestDTO {

    private double temperature;
    private double humidity;
    private double rain_mm;
    private double precipitation_mm;
    private int aqi;
    private double uv_index;
    private double cloud_cover;
    public double[] toFeatureArray() {
        return new double[]{
            temperature, humidity, rain_mm, precipitation_mm, (double) aqi, uv_index, cloud_cover
        };
    }
}

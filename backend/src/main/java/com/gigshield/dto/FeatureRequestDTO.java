package com.gigshield.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class FeatureRequestDTO {

    public static FeatureRequestDTOBuilder builder() {
        return new FeatureRequestDTOBuilder();
    }

    public static class FeatureRequestDTOBuilder {
        private double temp;
        private double hum;
        private double rain;
        private double precipScale;
        private int aqiVal;
        private double uv;
        private double cloud;
        private double zRain;
        private double zTemp;
        private double zAqi;
        private boolean spoof;
        private Integer obs30d;
        private Integer tenure;
        private Double earns;
        private String city;
        private String zone;

        public FeatureRequestDTOBuilder temperature(double v) { this.temp = v; return this; }
        public FeatureRequestDTOBuilder humidity(double v) { this.hum = v; return this; }
        public FeatureRequestDTOBuilder rain_mm(double v) { this.rain = v; return this; }
        public FeatureRequestDTOBuilder precipitation_mm(double v) { this.precipScale = v; return this; }
        public FeatureRequestDTOBuilder aqi(int v) { this.aqiVal = v; return this; }
        public FeatureRequestDTOBuilder uv_index(double v) { this.uv = v; return this; }
        public FeatureRequestDTOBuilder cloud_cover(double v) { this.cloud = v; return this; }
        public FeatureRequestDTOBuilder zone_avg_rain_mm(double v) { this.zRain = v; return this; }
        public FeatureRequestDTOBuilder zone_max_temp(double v) { this.zTemp = v; return this; }
        public FeatureRequestDTOBuilder zone_avg_aqi(double v) { this.zAqi = v; return this; }
        public FeatureRequestDTOBuilder gps_spoofed(boolean v) { this.spoof = v; return this; }
        public FeatureRequestDTOBuilder observed_count_30d(Integer v) { this.obs30d = v; return this; }
        public FeatureRequestDTOBuilder tenure_weeks(Integer v) { this.tenure = v; return this; }
        public FeatureRequestDTOBuilder avg_earnings(Double v) { this.earns = v; return this; }
        public FeatureRequestDTOBuilder city(String v) { this.city = v; return this; }
        public FeatureRequestDTOBuilder zone(String v) { this.zone = v; return this; }

        public FeatureRequestDTO build() {
            FeatureRequestDTO dto = new FeatureRequestDTO(temp, hum, rain, precipScale, aqiVal, uv, cloud, zRain, zTemp, zAqi, spoof);
            dto.setObserved_count_30d(obs30d);
            dto.setTenure_weeks(tenure);
            dto.setAvg_earnings(earns);
            dto.setCity(city);
            dto.setZone(zone);
            return dto;
        }
    }

    private double temperature;
    private double humidity;
    private double rain_mm;
    private double precipitation_mm;
    private int aqi;
    private double uv_index;
    private double cloud_cover;
    private double zone_avg_rain_mm;
    private double zone_max_temp;
    private double zone_avg_aqi;
    private boolean gps_spoofed;
    
    private Integer observed_count_30d;
    private Integer tenure_weeks;
    private Double avg_earnings;
    private String city;
    private String zone;

    public Integer getObserved_count_30d() { return observed_count_30d; }
    public void setObserved_count_30d(Integer v) { this.observed_count_30d = v; }
    public Integer getTenure_weeks() { return tenure_weeks; }
    public void setTenure_weeks(Integer v) { this.tenure_weeks = v; }
    public Double getAvg_earnings() { return avg_earnings; }
    public void setAvg_earnings(Double v) { this.avg_earnings = v; }
    public String getCity() { return city; }
    public void setCity(String v) { this.city = v; }
    public String getZone() { return zone; }
    public void setZone(String v) { this.zone = v; }

    public double getRain_mm() { return rain_mm; }
    public boolean isGps_spoofed() { return gps_spoofed; }
    public double getTemperature() { return temperature; }
    public double getHumidity() { return humidity; }
    public double getPrecipitation_mm() { return precipitation_mm; }
    public int getAqi() { return aqi; }
    public double getUv_index() { return uv_index; }
    public double getCloud_cover() { return cloud_cover; }
    public double getZone_avg_rain_mm() { return zone_avg_rain_mm; }
    public double getZone_max_temp() { return zone_max_temp; }
    public double getZone_avg_aqi() { return zone_avg_aqi; }

    public FeatureRequestDTO(double temperature, double humidity, double rain_mm, 
                             double precipitation_mm, int aqi, double uv_index, double cloud_cover,
                             double zone_avg_rain_mm, double zone_max_temp, double zone_avg_aqi, 
                             boolean gps_spoofed) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.rain_mm = rain_mm;
        this.precipitation_mm = precipitation_mm;
        this.aqi = aqi;
        this.uv_index = uv_index;
        this.cloud_cover = cloud_cover;
        this.zone_avg_rain_mm = zone_avg_rain_mm;
        this.zone_max_temp = zone_max_temp;
        this.zone_avg_aqi = zone_avg_aqi;
        this.gps_spoofed = gps_spoofed;
    }

    public double[] toFeatureArray() {
        return new double[]{
            temperature, humidity, rain_mm, precipitation_mm, (double) aqi, uv_index, cloud_cover,
            zone_avg_rain_mm, zone_max_temp, zone_avg_aqi, (gps_spoofed ? 1.0 : 0.0)
        };
    }
}

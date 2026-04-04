
# GigShield ML Model - Insurance Eligibility Predictor

## Overview
ML model that predicts gig worker insurance eligibility and claim amounts based on real-time weather conditions.

## Features Used
- Temperature
- Humidity
- Rain (mm)
- Precipitation (mm)
- AQI (Air Quality Index)
- UV Index
- Cloud Cover

## Model Architecture
- **Classifier**: Random Forest (Eligibility Prediction)
- **Regressor**: Random Forest (Claim Amount Prediction)
- **Scalers**: StandardScaler for feature normalization

## API Endpoints

### POST /predict
Predicts insurance eligibility and claim amount

**Request Body:**
```json
{
    "temperature": 42.0,
    "humidity": 35.0,
    "rain_mm": 0,
    "precipitation_mm": 0,
    "aqi": 220,
    "uv_index": 9.5,
    "cloud_cover": 10
}

{
    "status": "success",
    "eligible": true,
    "confidence": 0.95,
    "claim_amount": 450.75
}

# GigShield Backend - Frontend Integration Guide 🚀

This document provides everything the frontend team needs to connect the React/Next.js UI to the autonomous risk engine.

---

## 🏗️ Core API Endpoints

### 1. Onboarding & Registration (Multi-Step Form)
Use this endpoint at **Step 3** of the "Registration" flow to create both the worker profile and activate their policy in one click.

*   **Endpoint**: `POST http://localhost:8080/api/v1/policies/register-full`
*   **Request Body**:
    ```json
    {
      "name": "Ravi Kumar",
      "phoneNumber": "9876543210",
      "city": "Bangalore",
      "platform": "BLINKIT", // BLINKIT, ZEPTO, SWIGGY_INSTAMART
      "upiId": "ravi@upi",
      "coverageFor": "HEAVY_RAIN", // HEAVY_RAIN, SEVERE_AQI, EXTREME_HEAT, etc.
      "tier": "STANDARD" // BASIC, STANDARD, PRO
    }
    ```

### 2. Login & Dashboard Fetch
When the worker enters their **Worker ID** on the login screen.

*   **Check Profile**: `GET http://localhost:8080/api/v1/workers/{id}`
*   **Check Active Policy**: `GET http://localhost:8080/api/v1/policies/worker/{id}/active`

### 3. Real-Time Risk & Weather Dashboard
Use these to populate the gauges and alerts on the rider's dashboard.

*   **Risk Score & Live Weather**: `GET http://localhost:8080/api/v1/ml/predict/{id}`
    *   *Returns*: Live risk multiplier (from XGBoost) + Current Weather/AQI from Weatherbit.
    *   *Note*: The `prediction` field is the dynamic multiplier (e.g., `0.85`, `1.2`).

### 4. Demo Mode (Simulation)
Use this to show the "Magic" of parametric insurance in your demo.

*   **Trigger Payout**: `POST http://localhost:8080/api/v1/payouts/simulate/{workerId}?trigger=HEAVY_RAIN&payoutAmount=350`
    *   *Effect*: Instantly creates a "PAID" claim and generates a mock Razorpay Transaction ID.

---

## 🛠️ Data Entities Reference

### Worker Structure
```json
{
  "id": 1,
  "name": "Ravi",
  "city": "Bangalore",
  "platform": "BLINKIT",
  "upiId": "ravi@upi"
}
```

### Policy Structure
```json
{
  "id": 10,
  "tier": "STANDARD",
  "basePremium": 59.0,
  "dynamicMultiplier": 1.15,
  "finalPremium": 67.85,
  "status": "ACTIVE",
  "weekStartDate": "2026-04-02",
  "weekEndDate": "2026-04-09"
}
```

---

## 🔒 Connectivity & CORS
- **Base URL**: `http://localhost:8080` (or `8081` if you changed the port).
- **CORS Enabled**: Backend is configured to allow requests from `http://localhost:5500` and `http://localhost:3000`.
- **JSON Format**: Ensure you set `Content-Type: application/json` in your fetch headers.

> [!TIP]
> If your frontend still shows "Could not connect to server," verify that the backend is running and that you are using the correct port defined in `application.properties`.

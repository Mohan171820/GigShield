# GigShield
#  GigShield — AI-Powered Parametric Income Insurance for India's Gig Economy

> **Protecting the livelihoods of India's Q-Commerce delivery partners from uncontrollable external disruptions.**

---

##  Table of Contents

1. [Project Overview](#project-overview)
2. [Persona & Scenarios](#persona--scenarios)
3. [Application Workflow](#application-workflow)
4. [Weekly Premium Model](#weekly-premium-model)
5. [Parametric Triggers](#parametric-triggers)
6. [Platform Justification (Web vs Mobile)](#platform-justification)
7. [AI/ML Integration Plan](#aiml-integration-plan)
8. [Tech Stack](#tech-stack)
9. [Development Plan (6 Weeks)](#development-plan)
10. [Repository Structure](#repository-structure)

---

##  Project Overview

**GigShield** is an AI-enabled parametric insurance platform built exclusively for **Q-Commerce / Grocery delivery partners** (Zepto, Blinkit, Swiggy Instamart). These workers are the last-mile heroes who face income loss daily due to extreme weather, severe air pollution, local curfews, and zone-level disruptions — all completely outside their control.

Unlike traditional insurance that requires manual claim filing, medical proof, or lengthy audits, **GigShield pays automatically** when a pre-defined external disruption event is verified by real-time data. No paperwork. No waiting. Just protection.

**What we insure:** Loss of daily/weekly income due to verified external disruptions only.

**What we strictly exclude:** Health, life, accident, vehicle repair, or any personal liability coverage.

---

##  Persona & Scenarios

### Chosen Persona: Q-Commerce Delivery Partner (Zepto / Blinkit / Swiggy Instamart)

**Why Q-Commerce?**
Q-Commerce workers operate in dense urban micro-zones with 10-minute delivery SLAs. They are hyperlocal (within 2–5 km radius), highly active between 7 AM–11 PM, and are disproportionately affected by sudden short-burst disruptions like a 2-hour rain storm or an AQI spike — events that can wipe out half a day's earnings.

---

###  Representative Profiles

#### Persona A — Ravi, 27, Bangalore (Blinkit Partner)
- **Earnings:** ₹600–₹900/day | ₹3,500–₹5,500/week
- **Zone:** Koramangala (high-density, flood-prone micro-zone)
- **Pain Point:** During the June–September monsoon, 3–4 days per month are unworkable due to waterlogging and heavy rain. He loses ₹1,500–₹2,000/month with zero safety net.
- **GigShield Scenario:** A heavy rain trigger (>35mm/hr for 2+ hrs) is detected for his zone → GigShield auto-initiates a claim → ₹350 payout credited to UPI within 2 hours.

#### Persona B — Sunita, 32, Delhi (Zepto Partner)
- **Earnings:** ₹500–₹700/day | ₹3,000–₹4,500/week
- **Zone:** Dwarka (severe AQI spikes during Nov–Jan)
- **Pain Point:** During the winter smog season, AQI exceeds 400+ for 3–5 days/month, making outdoor deliveries a health risk and reducing her active hours. She self-limits work but has no financial buffer.
- **GigShield Scenario:** AQI for Dwarka crosses 350 (Severe category) for >4 consecutive hours → Parametric trigger fires → ₹280 payout credited automatically.

#### Persona C — Arjun, 24, Mumbai (Swiggy Instamart Partner)
- **Earnings:** ₹700–₹1,000/day | ₹4,500–₹6,500/week
- **Zone:** Andheri West (cyclone/storm surge risk in June–Oct)
- **Pain Point:** Mumbai cyclone warnings result in platform shutdowns or extremely low order volumes. When the platform goes dark, his income goes to zero.
- **GigShield Scenario:** IMD issues a Yellow/Orange cyclone alert for Mumbai region → GigShield detects platform activity drop + weather alert → Trigger fires → ₹500 payout processed.

---

##  Application Workflow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         GIGSHIELD PLATFORM FLOW                         │
└─────────────────────────────────────────────────────────────────────────┘

  ┌──────────────┐    ┌──────────────┐    ┌───────────────┐    ┌──────────────┐
  │  ONBOARDING  │───▶│ RISK PROFILE │───▶│ POLICY SETUP  │───▶│  COVERAGE    │
  │              │    │ & PRICING    │    │ (Weekly)      │    │  ACTIVE      │
  └──────────────┘    └──────────────┘    └───────────────┘    └──────────────┘
         │                   │                    │                    │
         ▼                   ▼                    ▼                    ▼
  • Phone + UPI ID    • Zone selection      • Select weekly       • Real-time
  • City & Zone       • Earnings input        plan tier             monitoring
  • Platform app      • XGBoost model       • Auto-renew UPI      • Weather APIs
    (Zepto/Blinkit)   • Risk score          • Premium deducted    • AQI feeds
  • Working hours     • Premium output        every Monday        • Alert systems


                              ┌─────────────────────────────┐
                              │    DISRUPTION MONITORING     │
                              │  (Real-time, 24x7 daemon)   │
                              └─────────────────────────────┘
                                            │
                              ┌─────────────▼─────────────┐
                              │   Trigger Threshold Met?   │
                              └─────────────┬─────────────┘
                                   YES      │      NO
                              ┌─────────────▼─────────────┐
                              │    FRAUD DETECTION ENGINE  │
                              │  • Location validation     │
                              │  • Activity cross-check    │
                              │  • Anomaly scoring         │
                              └─────────────┬─────────────┘
                                            │
                              ┌─────────────▼─────────────┐
                              │     AUTO CLAIM INITIATED   │
                              │   (No user action needed)  │
                              └─────────────┬─────────────┘
                                            │
                              ┌─────────────▼─────────────┐
                              │     PAYOUT PROCESSING      │
                              │   UPI / IMPS / Wallet      │
                              │   Within 2–4 hours         │
                              └───────────────────────────┘
```

### Step-by-Step Workflow

**Step 1 — Onboarding (< 3 minutes)**
- Worker enters mobile number, UPI ID, city, delivery zone, and primary platform (Zepto/Blinkit/Instamart).
- Uploads a soft-KYC document (platform partner ID or Aadhaar last 4 digits) for identity anchoring.
- Selects average weekly working hours (10–30 hrs, 30–50 hrs, 50+ hrs).

**Step 2 — Risk Profiling**
- XGBoost model ingests: zone-level historical weather data, historical AQI averages, historical disruption frequency, platform density score, and declared working hours.
- Outputs a **Risk Score (0–100)** and maps the worker to a premium tier.

**Step 3 — Policy Creation**
- Worker selects a weekly plan (Basic / Standard / Pro) based on their risk tier.
- UPI mandate is set up for automated weekly premium deduction (every Monday).
- Digital policy card is issued instantly.

**Step 4 — Live Monitoring**
- Backend daemon continuously monitors Weather APIs, AQI feeds, government alert systems, and (simulated) platform activity signals.
- Disruption events are matched against policyholder zones.

**Step 5 — Parametric Trigger & Auto Claim**
- When a trigger condition is verified, the system auto-initiates a claim for all active policyholders in the affected zone.
- Fraud engine validates the claim silently in the background.

**Step 6 — Payout**
- Approved payout is transferred to the registered UPI ID within 2–4 hours.
- Worker receives a push notification with claim details.

**Step 7 — Analytics Dashboard**
- Operators see real-time metrics: active policies, triggered events, payout volumes, fraud flags, zone-wise risk maps.

---

##  Weekly Premium Model

### Philosophy
Q-Commerce workers are paid daily or weekly by their platforms. A monthly premium model creates cash-flow friction. **GigShield charges weekly, deducted every Monday morning**, aligning with the worker's earning rhythm.

### Premium Tiers

| Tier | Profile | Weekly Premium | Max Weekly Payout | Best For |
|------|---------|---------------|-------------------|----------|
| **Basic** | Low-risk zone, part-time (< 30 hrs/wk) | ₹29 | ₹350 | New workers, low-disruption cities |
| **Standard** | Medium-risk zone, full-time (30–50 hrs/wk) | ₹59 | ₹700 | Most active urban partners |
| **Pro** | High-risk zone, heavy earner (50+ hrs/wk) | ₹99 | ₹1,200 | Metro workers in monsoon/smog zones |

### Dynamic Pricing Logic (XGBoost Model Outputs)

The base tier price is adjusted by a **Dynamic Multiplier (0.8× – 1.5×)** based on:

```
Dynamic Premium = Base Tier Price × Risk Multiplier

Risk Multiplier is driven by:
  + Zone's 3-month disruption frequency (historical trigger rate)
  + Seasonal factor (monsoon month = +20%, winter smog month = +15%)
  + Worker's individual claim history (no prior claims = -10% loyalty discount)
  + Platform density in zone (more workers = better risk pool = lower multiplier)
```

**Example Calculation:**
> Ravi (Bangalore, Standard Tier) in July (peak monsoon)
> Base = ₹59 | Seasonal Factor = +20% | Zone Disruption Score = High (+10%)
> **Final Premium = ₹59 × 1.30 = ₹77 for that week**

### Why Weekly (Not Monthly or Daily)?

| Frequency | Problem |
|-----------|---------|
| **Daily** | Too frequent for UPI mandates; high operational cost |
| **Monthly** | Workers cannot afford a lump sum; out of sync with weekly earnings |
| **Weekly ** | Matches platform payout cycle; affordable micro-payments; easy to pause/resume |

---

##  Parametric Triggers

Parametric insurance pays based on **objective, verifiable event thresholds** — not subjective loss assessment. This eliminates fraud and delays.

### Trigger Matrix (Q-Commerce Persona)

| Trigger Category | Event | Threshold | Data Source | Payout % of Weekly Max |
|-----------------|-------|-----------|-------------|------------------------|
| **Heavy Rain** | Rainfall rate | > 35 mm/hr sustained for ≥ 2 hours | OpenWeatherMap API | 50% |
| **Extreme Rain / Flood** | Rainfall rate | > 65 mm/hr OR IMD Red Alert issued | IMD API + OpenWeatherMap | 100% |
| **Severe Air Pollution** | AQI (PM2.5) | AQI > 350 (Severe) for ≥ 4 hours | CPCB AQI API / IQAir | 40% |
| **Extreme Heat** | Temperature | > 44°C for ≥ 3 hours during peak hours (10AM–4PM) | OpenWeatherMap API | 30% |
| **Cyclone / Storm Alert** | IMD Warning Level | Yellow Alert or above for the worker's city | IMD API | 75% |
| **Curfew / Civil Shutdown** | Government-declared zone closure | Official notification via NDMA / State APIs | NDMA / Mock API | 100% |
| **Platform Outage** | Delivery app downtime | Platform API returns 0 active orders for zone ≥ 2 hours | Mock Platform API | 50% |

### Trigger Rules
- Triggers are **zone-specific**, not city-wide (Koramangala ≠ all of Bangalore).
- Multiple triggers in one day use the **highest applicable payout**, not a sum.
- Maximum **2 trigger payouts per week** per policy to prevent stacking abuse.
- All trigger data is logged immutably for audit purposes.

---

##  Platform Justification

### Decision: **Progressive Web App (PWA) — Web-first, Mobile-Optimized**

| Factor | Web (PWA)  | Native Mobile App |
|--------|------------|-------------------|
| **Installation friction** | Zero — opens in browser | Requires app store download |
| **Device compatibility** | Works on any Android/iOS browser | Needs separate builds |
| **Update deployment** | Instant, no user action | Requires app store review |
| **Offline capability** | Supported via Service Workers | Native advantage |
| **Development cost** | Single codebase (React JS) | 2× cost for iOS + Android |
| **Target user reality** | Most delivery workers use Chrome on Android | Many avoid downloading new apps |

**React JS PWA** gives us native-like UX (home screen install, push notifications, offline mode) without the overhead of a separate mobile codebase. This is the right choice for a hackathon prototype that needs to demonstrate end-to-end flow quickly.

**Operator Dashboard** is a full web dashboard built in React, targeting desktop browsers for insurance operations teams.

---

##  AI/ML Integration Plan

### 1. Risk Scoring & Dynamic Premium Calculation — XGBoost Model

**Model:** XGBoost Classifier/Regressor trained on synthetic + historical disruption data.

**Input Features:**
```
- Zone-level features:
    zone_disruption_frequency_90d    (float)   # how often zone triggered in past 90 days
    zone_avg_monthly_rainfall_mm     (float)   # historical rainfall average
    zone_avg_winter_aqi              (float)   # historical winter AQI
    zone_delivery_density_score      (float)   # proxy for risk pool size
    city_cyclone_risk_score          (float)   # coastal proximity risk

- Worker-level features:
    weekly_active_hours              (int)     # self-declared working hours
    months_on_platform               (int)     # experience level
    prior_claims_count               (int)     # claim history
    avg_orders_per_hour              (float)   # earnings intensity proxy
    account_age_weeks                (int)     # trust score input

- Temporal features:
    is_monsoon_month                 (bool)    # June–September
    is_winter_smog_month             (bool)    # November–January
    week_of_year                     (int)     # seasonal encoding
```

**Output:**
```
risk_score        (0–100)    # composite risk score
premium_tier      (Basic/Standard/Pro)
dynamic_multiplier (0.8–1.5)  # applied to base tier premium
```

**Training Strategy:**
- Synthetic dataset generated using historical weather/AQI patterns for Bangalore, Delhi, Mumbai, Chennai, Hyderabad.
- Ground truth labels: "disruption occurred in zone in that week" (binary).
- Model is retrained monthly with new trigger event data.
- Served via a Spring Boot REST endpoint with model serialized as `.model` file (XGBoost4J).

---

### 2. Fraud Detection Engine — Anomaly Detection

**Approach:** Rule-based layer + Isolation Forest anomaly detection (secondary ML layer).

**Fraud Signals Monitored:**
```
Signal                          Check Logic
──────────────────────────────────────────────────────────────────
Location mismatch               Worker's registered zone ≠ GPS ping at trigger time
Inactive account claiming        Worker had zero logins in past 7 days before trigger
Duplicate zone flooding          >30% of zone's workers claim simultaneously (bot signal)
Rapid policy + claim pattern     Policy created < 48 hrs before first claim
AQI/rain data manipulation       External data cross-referenced with 2 independent APIs
Claim velocity                   > 2 claims in 7 days (enforced hard cap)
```

**Isolation Forest Layer:**
- Runs on each incoming claim batch.
- Features: claim timestamp delta, zone claim density, worker activity score, device fingerprint hash.
- Flags outliers for manual review; does not auto-reject.

---

### 3. Predictive Risk Alerts (Proactive Model)

- A lightweight **time-series forecast model** (using weather forecast APIs + historical patterns) predicts the probability of a trigger event for each zone in the next 48 hours.
- If P(trigger) > 60%, the system pre-alerts operational teams and pre-computes expected payout exposure.
- Helps with **capital reserve management** — the platform knows in advance how much to keep liquid.

---

### 4. Natural Language Claims Chatbot (Stretch Goal)

- A simple LLM-backed chatbot (via Anthropic API) to answer policyholder queries: "Was there a trigger in my zone today?", "When will my payout arrive?", "How is my premium calculated?"
- Reduces support overhead and improves worker trust.

---

##  Tech Stack

### Frontend — React JS (PWA)

| Layer | Technology |
|-------|-----------|
| Framework | React 18 + Vite |
| Styling | Tailwind CSS |
| State Management | Zustand |
| Charts & Analytics | Recharts |
| Maps (Zone Visualization) | Leaflet.js + react-leaflet |
| Push Notifications | Firebase Cloud Messaging (FCM) |
| PWA | Vite PWA Plugin + Service Workers |
| HTTP Client | Axios |

### Backend — Spring Boot

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.x (Java 17) |
| REST API | Spring Web MVC |
| Database | PostgreSQL (via Spring Data JPA) |
| Caching | Redis (for zone-level trigger state) |
| ML Inference | XGBoost4J (embedded in Spring Boot service) |
| Scheduler | Spring @Scheduled (trigger monitoring daemon) |
| Messaging | RabbitMQ (claim processing queue) |
| Authentication | Spring Security + JWT |
| Payment (Mock) | Razorpay Sandbox / UPI Mock API |

### ML / Data

| Component | Technology |
|-----------|-----------|
| Model Training | Python — XGBoost, scikit-learn, pandas |
| Anomaly Detection | Python — scikit-learn Isolation Forest |
| Model Serialization | XGBoost native `.model` format (loaded via XGBoost4J in JVM) |
| Feature Pipeline | Python scripts → JSON feature store |
| Data Storage | PostgreSQL + TimescaleDB extension (time-series trigger logs) |

### External APIs & Integrations

| Integration | API / Service | Purpose |
|------------|--------------|---------|
| Weather | OpenWeatherMap API (free tier) | Rain, temperature triggers |
| AQI | CPCB AQI API / IQAir API | Pollution triggers |
| Weather Alerts | IMD API (mock for hackathon) | Cyclone / storm alerts |
| Geolocation | Google Maps Geocoding API | Zone boundary validation |
| Platform Activity | Mock REST API (simulated) | Platform outage detection |
| Payments | Razorpay Sandbox | UPI payout simulation |
| Notifications | Firebase FCM | Push alerts to workers |

### Infrastructure (Prototype)

| Component | Tool |
|-----------|------|
| Containerization | Docker + Docker Compose |
| API Gateway | Spring Cloud Gateway |
| Local Dev | Docker Compose (all services) |
| CI/CD | GitHub Actions |
| Hosting (Demo) | Render.com / Railway.app |

---

##  Development Plan (6 Weeks)

### Week 1 — Foundation & Idea Document *(Current Phase)*
- [x] Problem analysis and persona definition (Q-Commerce)
- [x] Trigger matrix design
- [x] Weekly premium model design
- [x] README documentation
- [ ] Repository setup with folder structure
- [ ] 2-minute strategy video
- [ ] Basic React shell (landing page + onboarding form wireframe)
- [ ] Spring Boot project initialization with base entity models

### Week 2 — Core Backend + ML Model
- [ ] PostgreSQL schema: `workers`, `policies`, `triggers`, `claims`, `payouts`
- [ ] Worker onboarding REST API (POST /api/onboard)
- [ ] XGBoost model training script (Python) on synthetic data
- [ ] XGBoost4J integration in Spring Boot — /api/risk-score endpoint
- [ ] Dynamic premium calculation service
- [ ] Mock Weather API integration (OpenWeatherMap)
- [ ] Mock AQI API integration

### Week 3 — Parametric Engine + Fraud Detection
- [ ] Trigger monitoring daemon (Spring @Scheduled, runs every 15 minutes)
- [ ] Zone-level trigger evaluation logic
- [ ] Auto claim initiation service
- [ ] Fraud detection rule engine
- [ ] Isolation Forest anomaly model (Python → Spring Boot integration)
- [ ] Claims queue (RabbitMQ consumer)

### Week 4 — Frontend: Worker App
- [ ] Onboarding flow (mobile-responsive, PWA)
- [ ] Policy dashboard (active coverage, premium history)
- [ ] Claim status tracker (real-time updates via polling)
- [ ] Payout history screen
- [ ] Push notification integration (FCM)

### Week 5 — Frontend: Operator Dashboard + Payments
- [ ] Analytics dashboard (Recharts — zone risk map, payout volume, trigger history)
- [ ] Fraud flagging panel
- [ ] Razorpay sandbox integration for payout simulation
- [ ] UPI mandate setup simulation for premium deduction
- [ ] End-to-end test: Onboard → Trigger → Claim → Payout

### Week 6 — Polish, Demo & Submission
- [ ] Full integration testing
- [ ] Seed database with realistic demo scenarios (Ravi, Sunita, Arjun personas)
- [ ] Demo video recording (platform walkthrough)
- [ ] Performance optimization
- [ ] Deployment to cloud demo environment
- [ ] Final presentation preparation

---

##  Repository Structure

```
gigshield/
│
├── frontend/                          # React JS PWA
│   ├── src/
│   │   ├── components/
│   │   │   ├── onboarding/            # Worker registration flow
│   │   │   ├── dashboard/             # Worker policy dashboard
│   │   │   ├── claims/                # Claim status tracker
│   │   │   ├── operator/              # Analytics dashboard
│   │   │   └── shared/                # Reusable UI components
│   │   ├── pages/
│   │   ├── store/                     # Zustand state management
│   │   ├── api/                       # Axios API client
│   │   └── utils/
│   ├── public/
│   ├── index.html
│   └── vite.config.js
│
├── backend/                           # Spring Boot Application
│   └── src/main/java/com/gigshield/
│       ├── controller/                # REST controllers
│       │   ├── WorkerController.java
│       │   ├── PolicyController.java
│       │   ├── ClaimController.java
│       │   └── DashboardController.java
│       ├── service/                   # Business logic
│       │   ├── RiskScoringService.java
│       │   ├── TriggerMonitorService.java
│       │   ├── FraudDetectionService.java
│       │   ├── PayoutService.java
│       │   └── PremiumCalculationService.java
│       ├── model/                     # JPA Entities
│       │   ├── Worker.java
│       │   ├── Policy.java
│       │   ├── Claim.java
│       │   └── TriggerEvent.java
│       ├── repository/                # Spring Data JPA Repos
│       ├── scheduler/                 # Cron jobs for trigger monitoring
│       ├── ml/                        # XGBoost4J integration
│       │   └── XGBoostModelService.java
│       └── config/                    # Security, Redis, RabbitMQ configs
│
├── ml/                                # Python ML Pipeline
│   ├── data/
│   │   ├── synthetic_data_gen.py      # Synthetic training data generator
│   │   └── historical_zones.csv       # Zone risk historical data
│   ├── models/
│   │   ├── train_xgboost.py           # XGBoost training script
│   │   ├── train_isolation_forest.py  # Fraud anomaly model
│   │   └── risk_model.model           # Serialized XGBoost model
│   ├── features/
│   │   └── feature_engineering.py    # Feature pipeline
│   └── notebooks/
│       └── eda_zone_analysis.ipynb   # Exploratory analysis
│
├── api-mocks/                         # Mock external API servers
│   ├── weather-mock/                  # OpenWeatherMap mock
│   ├── aqi-mock/                      # AQI API mock
│   └── platform-mock/                 # Zepto/Blinkit activity mock
│
├── docker-compose.yml                 # Local dev environment
├── .github/
│   └── workflows/
│       └── ci.yml                     # GitHub Actions CI
└── README.md                          # This file
```

---

##  Key Differentiators

| Feature | GigShield | Traditional Insurance |
|---------|-----------|----------------------|
| Claim process | **Fully automated, zero user action** | Manual form submission |
| Payout time | **2–4 hours** | 7–30 days |
| Premium cycle | **Weekly (₹29–₹99)** | Monthly / Annual |
| Coverage basis | **Objective parametric triggers** | Subjective loss assessment |
| Fraud detection | **AI-powered, real-time** | Manual audits |
| Onboarding | **< 3 minutes, mobile-first** | Branch visit / broker |

---

##  Golden Rules Compliance

| Rule | Our Approach |
|------|-------------|
| ✅ Single Persona | Q-Commerce delivery partners (Zepto, Blinkit, Swiggy Instamart) |
| ✅ Income Loss Only | All triggers result in income loss payouts only — no vehicle, health, or accident coverage |
| ✅ Weekly Pricing | Premium deducted every Monday via UPI mandate; all tiers priced weekly (₹29/₹59/₹99) |

---

##  Team

> Shri Raksha 
> Avisha Catherine
> M.Mohan Murali
> Taqueer Ahamad

---

##  License

This project is developed for hackathon purposes. All rights reserved by the team.

---

*Built with ❤️ to protect India's gig workers — because their hustle deserves a safety net.*

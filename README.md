<div align="center">

<img src="https://img.shields.io/badge/ InSureGig-AI%20Parametric%20Insurance-FF6B2B?style=for-the-badge&logo=shield&logoColor=white"/>

# 🛡️ InSureGig 
### *No paperwork. No waiting. Just protection.*

*P.S-Sometimes while logging you, you may face an issue where it takes longer to direct to the user. It happens sometimes because of rendering. Mostly this doesn't occurs, it is very rare but if occurred please don't panic wait for few mins or refresh and retry*

AI-powered parametric income insurance for India's **Q-Commerce delivery partners** —  
auto-triggered payouts when weather, pollution, or shutdowns halt their hustle.

[!HTML,CSS,JS]
[![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.x-Java_17-6DB33F?style=flat-square&logo=springboot)](https://spring.io)
[![XGBoost](https://img.shields.io/badge/XGBoost-Risk_Model-0073B7?style=flat-square)](https://xgboost.ai)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-TimescaleDB-336791?style=flat-square&logo=postgresql)](https://postgresql.org)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)](https://docker.com)

</div>

---

## 🎯 The Problem in 10 Seconds

> A Blinkit rider earns ₹700/day.  
> A 3-hour monsoon wipes that out.  
> He has **zero** safety net. Zero.

 InSureGig fixes this with **parametric triggers** — when rain/AQI/curfew crosses a verified threshold, money moves to the worker's UPI **automatically**. No claim form. No adjuster. No delay.

**What's excluded (strictly):** health · life · accidents · vehicle repair → **income loss only.**

---

## 👤 Who We Protect

**Persona:** Q-Commerce delivery partners — Zepto, Blinkit, Swiggy Instamart

> *Why Q-Commerce?* Hyperlocal 2–5 km zones, 7AM–11PM shifts, burst disruptions. A 2-hr storm = half a day's income gone. Most exposed, least protected.

| | Ravi, 27 · Bangalore | Sunita, 32 · Delhi | Arjun, 24 · Mumbai |
|---|---|---|---|
| **Platform** | Blinkit | Zepto | Swiggy Instamart |
| **Zone** | Koramangala | Dwarka | Andheri West |
| **Weekly Earn** | ₹3,500–5,500 | ₹3,000–4,500 | ₹4,500–6,500 |
| **Pain** | Monsoon flooding | Winter AQI 400+ | Cyclone shutdowns |
| **Trigger** | Rain >35mm / 2hrs | AQI >350 / 4hrs | IMD Yellow Alert |
| **Auto-Payout** | ₹350 → UPI in 2hrs | ₹280 → UPI in 2hrs | ₹500 → UPI in 2hrs |

---
🛡️* ##The "Star" Feature: 2-Level Payout Protection*
To prevent fraud while maintaining lightning-fast payouts, we implemented a unique Dual-Layer Verification System:

Level 1: The ML Shield (Automated Cluster Validation)
When workers raise complaints (e.g., "I can't deliver due to rain in Zone A"), our ML model doesn't just look at one person. It analyzes spatial clusters.

The Logic: If 50 workers from Zone A all report disruptions simultaneously during a recorded rain event, the ML model flags this as a "Legitimate Cluster."
The Action: It automatically prepares an "Auto-Pay" batch, verifying that the complaint density matches the weather severity in that specific zone.
Level 2: Human-in-the-Loop (Admin Verification)
To ensure 100% integrity before the money leaves the bank, these ML-validated clusters are presented to a human Admin.

The Logic: The Admin sees the ML's recommendation: "98% Confidence: Heavy Rain in Zone A affecting 120 workers."
The Action: The Admin does a final sanity check (cross-referencing with live weather feeds or local reports) and clicks "Approve Auto-Pay." This turns a process that usually takes weeks into one that takes minutes.


## ⚡ Platform Flow

```
ONBOARD (3 min)  →  RISK SCORE  →  WEEKLY POLICY  →  COVERED 24x7
Phone + UPI ID       XGBoost AI      ₹29/₹59/₹99       Live monitoring
City + Zone          0–100 score     UPI auto-deduct    All trigger types
Working hours        Premium tier    Every Monday
      │                                                        │
      └──────────────── Disruption detected ──────────────────┘
                                │
                   Fraud engine validates silently
                                │
                       Claim auto-initiated
                                │
                    💸 UPI payout in 2–4 hours
```

---

## 💰 Weekly Premium Model

> Workers earn weekly → we charge weekly. Deducted every **Monday via UPI mandate**.

| Tier | Weekly Premium | Max Weekly Payout | For |
|------|:-:|:-:|---|
| 🟢 **Basic** | ₹29 | ₹350 | Part-time · Low-risk zone |
| 🟡 **Standard** | ₹59 | ₹700 | Full-time · Urban partner |
| 🔴 **Pro** | ₹99 | ₹1,200 | Heavy earner · Metro zone |

**Dynamic multiplier (0.8× – 1.5×) via XGBoost:**

```
Final Premium = Base Tier × Risk Multiplier

  📊 Zone disruption history (90d)     🌧️ Seasonal: +20% monsoon / +15% smog
  🎖️ No prior claims → −10% discount   👥 Larger zone pool → lower multiplier

  Example: Ravi in July → ₹59 × 1.30 = ₹77/week
```

---

## ⚡ Parametric Triggers

*Objective thresholds. Verified data. Zero subjectivity.*

| Trigger | Threshold | Source | Payout |
|---------|-----------|--------|:------:|
| 🌧️ Heavy Rain | >35mm/hr for ≥2 hrs | OpenWeatherMap | 50% |
| 🌊 Extreme Rain / Flood | >65mm/hr OR IMD Red Alert | IMD + OWM | 100% |
| 😷 Severe Pollution | AQI >350 for ≥4 hrs | CPCB / IQAir | 40% |
| 🌡️ Extreme Heat | >44°C for ≥3 hrs (10AM–4PM) | OpenWeatherMap | 30% |
| 🌀 Cyclone Alert | IMD Yellow Alert or above | IMD API | 75% |
| 🚫 Curfew / Shutdown | Official zone closure declared | NDMA / Mock | 100% |
| 📵 Platform Outage | 0 active orders for ≥2 hrs | Platform Mock API | 50% |

> Zone-specific (Koramangala ≠ all of Bangalore) · Max 2 payouts/week · Highest trigger wins, no stacking · All events logged immutably

---

## 🤖 AI / ML Architecture

### 1️⃣ XGBoost — Risk Scorer & Premium Engine

```
15 Input Features                       Output
─────────────────────────────           ──────────────────────────────
zone_disruption_freq_90d          →     risk_score         (0–100)
zone_avg_rainfall_mm                    premium_tier       Basic/Std/Pro
zone_avg_winter_aqi                     dynamic_multiplier (0.8–1.5×)
city_cyclone_risk_score
weekly_active_hours
prior_claims_count
is_monsoon_month / is_smog_month
...

Dataset: 10,000 synthetic samples · Retrained monthly
Inference: XGBoost4J embedded in Spring Boot (no external Python call)
```

### 2️⃣ Isolation Forest — Fraud Detection

```
Signals monitored:
  📍 Location mismatch       (registered zone ≠ GPS at trigger time)
  💤 Inactive account claim  (zero logins in 7 days pre-trigger)
  🤖 Zone flooding           (>30% of zone workers claim simultaneously)
  ⚡ Rapid policy + claim    (policy created <48hrs before first claim)
  🔁 Claim velocity          (>2 claims/week = hard cap enforced)

→ Flags anomalies for review. Never auto-rejects. Fully auditable.
```

### 3️⃣ Predictive Risk Alerts

```
48-hour zone trigger probability forecast
P(trigger) > 60%  →  Pre-alert ops team + pre-compute payout exposure
                      Keeps capital reserves liquid before big events
```

---

## 🏗️ Tech Stack

```
┌──────────────────────────────────────────────────────────────┐
│ FRONTEND    HTML5,CSS3,JS             │
├──────────────────────────────────────────────────────────────┤
│ BACKEND     Spring Boot 3.x · Java 17 · Spring Security/JWT  │
│             Spring @Scheduled · RabbitMQ · Redis              │
│             Razorpay Sandbox (UPI mock)                       │
├──────────────────────────────────────────────────────────────┤
│ ML          Python · XGBoost · scikit-learn · pandas          │
│             Isolation Forest · XGBoost4J (JVM inference)      │
│             10,000-sample synthetic dataset                   │
├──────────────────────────────────────────────────────────────┤
│ DATA        PostgreSQL + TimescaleDB (trigger time-series)    │
│             OpenWeatherMap · CPCB AQI · IMD · IQAir           │
├──────────────────────────────────────────────────────────────┤
│ INFRA       Docker Compose · GitHub Actions CI/CD · Railway   │
└──────────────────────────────────────────────────────────────┘
```

---

## 📁 Repository Structure

```
 InSureGig/
├── frontend/              
├── backend/               # Spring Boot — APIs, trigger daemon, payout engine
│   └── ml/                # XGBoost4J inference (embedded)
├── ml/                    # Python — model training & fraud detection
│   ├── train_xgboost.py
│   ├── train_isolation_forest.py
│   └── notebooks/         # EDA & feature analysis
├── api-mocks/             # Simulated Weather / AQI / Platform APIs
├── docker-compose.yml
└── README.md
```

---

## 📅 6-Week Roadmap

| Week | Theme | Key Deliverables |
|:----:|-------|-----------------|
| **1** ✅ | Foundation | README · Repo · React shell · Spring Boot init |
| **2** | Core Backend | DB schema · XGBoost training · Premium API · Weather mock |
| **3** | Trigger Engine | Monitoring daemon · Fraud rules · Isolation Forest · RabbitMQ |
| **4** | Worker PWA | Onboarding · Policy dashboard · Claim tracker · FCM push |
| **5** | Ops Dashboard | Analytics · Fraud panel · Razorpay sandbox · E2E test |
| **6** | Ship 🚀 | Integration tests · Demo personas · Video · Deploy |

---

## 🏆  InSureGig vs. Traditional Insurance

| Metric | 🛡️  InSureGig | Traditional |
|--------|:-----------:|:-----------:|
| Claim process | ✅ Zero user action | ❌ Manual form |
| Payout time | ✅ 2–4 hours | ❌ 7–30 days |
| Premium cycle | ✅ Weekly ₹29–₹99 | ❌ Annual lump sum |
| Fraud detection | ✅ AI real-time | ❌ Manual audit |
| Onboarding | ✅ 3 minutes | ❌ Branch visit |
| Coverage basis | ✅ Objective data trigger | ❌ Subjective loss claim |

---

## ✅ Golden Rules Compliance

| Rule | Status |
|------|--------|
| Single persona (Q-Commerce only) | ✅ Zepto · Blinkit · Swiggy Instamart |
| Income loss only | ✅ No vehicle · no health · no accidents |
| Weekly pricing model | ✅ ₹29/₹59/₹99 · UPI deduct every Monday |

---

## 👥 Team 

S.Shri Raksha . Avisha Catherine · M. Mohan Murali · Taqueer Ahamad

---

<div align="center">

*Built with ❤️ for India's gig workers — because their hustle deserves a safety net.*

</div>

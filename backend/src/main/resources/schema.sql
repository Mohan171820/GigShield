-- Ensure new columns exist in the workers table for Render deployment
ALTER TABLE workers ADD COLUMN IF NOT EXISTS plan VARCHAR(255);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS plan_type VARCHAR(255);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS coverage_amount FLOAT8;

-- Initialize existing records with default values
UPDATE workers SET plan = 'STANDARD' WHERE plan IS NULL;
UPDATE workers SET plan_type = 'STANDARD' WHERE plan_type IS NULL;
UPDATE workers SET coverage_amount = 3000.0 WHERE coverage_amount IS NULL;

-- Ensure new columns exist in the complaints table for audit trail
ALTER TABLE workers ADD COLUMN IF NOT EXISTS password VARCHAR(255);
UPDATE workers SET password = 'admin123' WHERE password IS NULL;

ALTER TABLE complaints ADD COLUMN IF NOT EXISTS ml_decision VARCHAR(255);
ALTER TABLE complaints ADD COLUMN IF NOT EXISTS ml_confidence FLOAT4;
ALTER TABLE complaints ADD COLUMN IF NOT EXISTS fraud_score FLOAT4;
ALTER TABLE complaints ADD COLUMN IF NOT EXISTS worker_activity_score FLOAT4;
ALTER TABLE complaints ADD COLUMN IF NOT EXISTS zone_weather_verified BOOLEAN;
ALTER TABLE complaints ADD COLUMN IF NOT EXISTS suggested_payout_amount INTEGER;
ALTER TABLE complaints ADD COLUMN IF NOT EXISTS device_fingerprint VARCHAR(255);
ALTER TABLE complaints ADD COLUMN IF NOT EXISTS ip_address VARCHAR(255);
ALTER TABLE complaints ADD COLUMN IF NOT EXISTS shared_device_flag BOOLEAN DEFAULT FALSE;
ALTER TABLE complaints ADD COLUMN IF NOT EXISTS policy_registered_at TIMESTAMP;
ALTER TABLE complaints ADD COLUMN IF NOT EXISTS coverage_cap_applied BOOLEAN;
ALTER TABLE complaints ADD COLUMN IF NOT EXISTS original_requested_amount FLOAT8;
ALTER TABLE complaints ADD COLUMN IF NOT EXISTS remaining_coverage_after FLOAT8;

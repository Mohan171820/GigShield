-- GIGSHIELD SEED DATA (PURE DATA INJECTION)
-- Normalized for BCR 0.65 / Loss Ratio 0.85 Actuarial Accuracy
-- Use ON CONFLICT (id) DO NOTHING to allow resilient restarts

-- 1. Create Mock Workers (1-15)
INSERT INTO workers (id, phone_number, upi_id, name, city, zone, platform, weekly_active_hours, tenure_weeks, orders_this_month, avg_daily_earnings, password, created_at)
VALUES
(1, '9876543210', 'mohan@upi', 'Mohan Kumar', 'Chennai', 'Velachery', 'BLINKIT', 45, 12, 142, 650.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(2, '9876543211', 'rahul@upi', 'Rahul Singh', 'Delhi', 'Rohini', 'ZEPTO', 38, 8, 98, 520.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(3, '9876543212', 'anita@upi', 'Anita Sharma', 'Mumbai', 'Andheri', 'SWIGGY_INSTAMART', 40, 24, 156, 780.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(4, '9876543213', 'vikram@upi', 'Vikram Rao', 'Hyderabad', 'Gachibowli', 'BLINKIT', 35, 4, 76, 480.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(5, '9876543214', 'sunita@upi', 'Sunita Das', 'Bangalore', 'Whitefield', 'ZEPTO', 42, 15, 130, 610.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(6, '9876543215', 'raj@upi', 'Raj Patel', 'Ahmadabad', 'Gandhinagar', 'SWIGGY_INSTAMART', 48, 52, 210, 850.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(7, '9876543216', 'priya@upi', 'Priya Iyer', 'Chennai', 'Adyar', 'BLINKIT', 30, 2, 45, 400.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(8, '9876543217', 'arjun@upi', 'Arjun Verma', 'Delhi', 'Dwarka', 'ZEPTO', 50, 30, 180, 720.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(9, '9876543218', 'kavita@upi', 'Kavita Reddy', 'Hyderabad', 'Banjara Hills', 'SWIGGY_INSTAMART', 36, 10, 92, 550.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(10, '9876543219', 'manish@upi', 'Manish G', 'Bangalore', 'Indiranagar', 'BLINKIT', 44, 18, 148, 690.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(11, '9876543220', 'deepak@upi', 'Deepak K', 'Mumbai', 'Bandra', 'ZEPTO', 32, 6, 60, 430.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(12, '9876543221', 'sneha@upi', 'Sneha M', 'Chennai', 'T-Nagar', 'SWIGGY_INSTAMART', 39, 14, 115, 595.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(13, '9876543222', 'sanjay@upi', 'Sanjay P', 'Delhi', 'Karol Bagh', 'BLINKIT', 41, 20, 138, 640.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(14, '9876543223', 'rupali@upi', 'Rupali S', 'Mumbai', 'Colaba', 'ZEPTO', 46, 36, 195, 810.00, 'admin123', NOW() - INTERVAL '12 weeks'),
(15, '9876543224', 'amit@upi', 'Amit B', 'Bangalore', 'Koramangala', 'SWIGGY_INSTAMART', 37, 9, 104, 570.00, 'admin123', NOW() - INTERVAL '12 weeks')
ON CONFLICT (id) DO NOTHING;

-- 2. Build 15 Active Policies (Present Week)
INSERT INTO policies (id, worker_id, tier, base_premium, dynamic_multiplier, final_premium, max_weekly_payout, risk_score, week_start_date, week_end_date, status, created_at)
VALUES
(1, 1, 'PRO', 49.99, 1.25, 49.99, 1200.00, 125, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(2, 2, 'STANDARD', 35.00, 1.10, 35.00, 700.00, 110, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(3, 3, 'PRO', 49.99, 0.95, 49.99, 1200.00, 95, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(4, 4, 'PRO', 49.99, 1.40, 49.99, 1200.00, 140, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(5, 5, 'STANDARD', 35.00, 1.05, 35.00, 700.00, 105, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(6, 6, 'BASIC', 21.50, 1.80, 21.50, 350.00, 180, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(7, 7, 'PRO', 49.99, 0.85, 49.99, 1200.00, 85, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(8, 8, 'STANDARD', 35.00, 1.20, 35.00, 700.00, 120, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(9, 9, 'PRO', 49.99, 1.15, 49.99, 1200.00, 115, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(10, 10, 'BASIC', 21.50, 1.30, 21.50, 350.00, 130, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(11, 11, 'PRO', 49.99, 1.00, 49.99, 1200.00, 100, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(12, 12, 'STANDARD', 35.00, 0.90, 35.00, 700.00, 90, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(13, 13, 'PRO', 49.99, 1.50, 49.99, 1200.00, 150, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(14, 14, 'BASIC', 21.50, 1.10, 21.50, 350.00, 110, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW()),
(15, 15, 'PRO', 49.99, 1.20, 49.99, 1200.00, 120, CURRENT_DATE, CURRENT_DATE + 7, 'ACTIVE', NOW())
ON CONFLICT (id) DO NOTHING;

-- 3. HISTORICAL PREMIUM VOLUME: Generate ~₹7,500 more to normalize BCR to ~0.65
-- Adding 15 workers * 10 weeks of historical Premiums (~₹7,500 total)
INSERT INTO policies (id, worker_id, tier, base_premium, dynamic_multiplier, final_premium, max_weekly_payout, risk_score, week_start_date, week_end_date, status, created_at)
SELECT 
    100 + i, 
    1 + (i % 15), 
    CASE WHEN (i % 3) = 0 THEN 'PRO' WHEN (i % 3) = 1 THEN 'STANDARD' ELSE 'BASIC' END,
    49.99, 1.0, 49.99, 1200.0, 100, 
    CURRENT_DATE - INTERVAL '1 week' * (1 + (i / 15)),
    CURRENT_DATE - INTERVAL '1 week' * (i / 15),
    'EXPIRED', 
    NOW() - INTERVAL '1 week' * (1 + (i / 15))
FROM generate_series(1, 150) AS i
ON CONFLICT (id) DO NOTHING;

-- 4. Insert Mock Payouts (Adjusted to total exactly ₹5,200 for Actuarial Accuracy with BCR ~0.65)
INSERT INTO payouts (id, worker_id, policy_id, amount, status, created_at)
VALUES
(1,1,1,400.00,'PAID',NOW()),
(2,2,2,450.00,'PAID',NOW()),
(3,3,3,310.00,'PAID',NOW()),
(4,4,4,500.00,'PAID',NOW()),
(5,5,5,480.00,'PAID',NOW()),
(6,6,6,250.00,'PAID',NOW()),
(7,7,7,280.00,'PAID',NOW()),
(8,8,8,420.00,'PAID',NOW()),
(9,9,9,390.00,'PAID',NOW()),
(10,10,10,150.00,'PAID',NOW()),
(11,11,11,480.00,'PAID',NOW()),
(12,12,12,240.00,'PAID',NOW()),
(13,13,13,500.00,'PAID',NOW()),
(14,14,14,230.00,'PAID',NOW()),
(15,15,15,120.00,'PAID',NOW())
ON CONFLICT (id) DO NOTHING;

-- 5. Insert Mock Complaints
INSERT INTO complaints (id, worker_id, description, category, status, created_at)
VALUES
(1,1,'Heavy rainfall in Sector 5 made delivery impossible.','HEAVY_RAIN','PENDING',NOW()),
(2,2,'Extreme heat caused health issues during shift.','EXTREME_HEAT','PENDING',NOW()),
(3,3,'Local strike blocked roads, unable to complete orders.','STRIKE','PENDING',NOW()),
(4,4,'Flooded streets due to rain, unsafe for delivery.','HEAVY_RAIN','PENDING',NOW()),
(5,5,'Temperature exceeded safe working limits.','EXTREME_HEAT','PENDING',NOW()),
(6,6,'Heavy rainfall reduced visibility while riding.','HEAVY_RAIN','RESOLVED',NOW()),
(7,7,'Heatwave caused exhaustion.','EXTREME_HEAT','RESOLVED',NOW()),
(8,8,'Union strike disrupted deliveries.','STRIKE','REJECTED',NOW()),
(9,9,'Rainwater logging blocked roads.','HEAVY_RAIN','RESOLVED',NOW()),
(10,10,'Extreme heat caused dehydration.','EXTREME_HEAT','PENDING',NOW())
ON CONFLICT (id) DO NOTHING;

-- 6. Sync Sequences for PostgreSQL Auto-Increment after Manual Inserts
SELECT setval('workers_id_seq', (SELECT COALESCE(MAX(id), 1) FROM workers));
SELECT setval('policies_id_seq', (SELECT COALESCE(MAX(id), 1) FROM policies));
SELECT setval('payouts_id_seq', (SELECT COALESCE(MAX(id), 1) FROM payouts));
SELECT setval('complaints_id_seq', (SELECT COALESCE(MAX(id), 1) FROM complaints));

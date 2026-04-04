from flask import Flask, request, jsonify
from flask_cors import CORS
import joblib
import pandas as pd
import numpy as np
from datetime import datetime
import warnings
warnings.filterwarnings('ignore')

# ============================================
# LOAD YOUR OLD MODEL FILES
# ============================================

print("="*60)
print("🚀 Starting Insurance Prediction API Server (OLD MODEL)")
print("="*60)

print("\n📦 Loading models...")

# Load your 4 old model files
classifier = joblib.load('insurance_classifier.pkl')      # For eligibility
regressor = joblib.load('claim_amount_regressor.pkl')    # For claim amount
scaler_clf = joblib.load('classifier_scaler.pkl')        # Scaler for classifier
scaler_reg = joblib.load('regressor_scaler.pkl')         # Scaler for regressor

print("✅ Models loaded successfully!")
print(f"   - Classifier: {type(classifier).__name__}")
print(f"   - Regressor: {type(regressor).__name__}")
print(f"   - Scalers: 2 loaded")

# Features your old model expects (same as before)
features = ['temperature', 'humidity', 'rain_mm', 'precipitation_mm', 
            'aqi', 'uv_index', 'cloud_cover']

# ============================================
# CREATE FLASK API SERVER
# ============================================

app = Flask(__name__)
CORS(app)  # Allow Java friend to connect from anywhere

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'status': 'healthy',
        'model': 'OLD_MODEL',
        'timestamp': datetime.now().isoformat(),
        'features_expected': features
    })

@app.route('/predict', methods=['POST'])
def predict():
    """
    Main prediction endpoint for Java backend
    Uses OLD MODEL with two scalers
    """
    try:
        # 1. Get JSON data from Java friend
        data = request.get_json()
        print(f"\n📥 Received request: {data}")
        
        # 2. Validate all required features
        missing_fields = [f for f in features if f not in data]
        if missing_fields:
            return jsonify({
                'status': 'error',
                'message': f'Missing fields: {missing_fields}',
                'required_fields': features
            }), 400
        
        # 3. Convert to DataFrame
        input_df = pd.DataFrame([data])
        
        # 4. Scale using CLASSIFIER scaler (for eligibility prediction)
        input_scaled_clf = scaler_clf.transform(input_df[features])
        
        # 5. Predict eligibility
        is_eligible = classifier.predict(input_scaled_clf)[0]
        probability = classifier.predict_proba(input_scaled_clf)[0, 1]
        
        # 6. Calculate claim amount if eligible
        claim_amount = 0.0
        if is_eligible == 1:
            # Scale using REGRESSOR scaler (for amount prediction)
            input_scaled_reg = scaler_reg.transform(input_df[features])
            claim_amount = regressor.predict(input_scaled_reg)[0]
            claim_amount = round(float(claim_amount), 2)
        
        # 7. Prepare response
        response = {
            'status': 'success',
            'eligible': bool(is_eligible),
            'confidence': float(probability),
            'claim_amount': claim_amount,
            'currency': 'INR',
            'timestamp': datetime.now().isoformat(),
            'message': f"{'✅ ELIGIBLE' if is_eligible else '❌ NOT ELIGIBLE'} - Claim amount: ₹{claim_amount}" if is_eligible else "No claim applicable"
        }
        
        print(f"📤 Response: {response}")
        return jsonify(response)
    
    except Exception as e:
        print(f"❌ Error: {str(e)}")
        return jsonify({
            'status': 'error',
            'message': str(e)
        }), 500

@app.route('/predict_batch', methods=['POST'])
def predict_batch():
    """
    Batch prediction for multiple weather data points
    """
    try:
        data = request.get_json()
        
        if 'records' not in data:
            return jsonify({'status': 'error', 'message': 'Missing records field'}), 400
        
        records = data['records']
        results = []
        
        for i, record in enumerate(records):
            # Check missing fields
            missing = [f for f in features if f not in record]
            if missing:
                results.append({
                    'index': i,
                    'error': f'Missing fields: {missing}',
                    'eligible': False,
                    'claim_amount': 0
                })
                continue
            
            # Make prediction
            input_df = pd.DataFrame([record])
            input_scaled_clf = scaler_clf.transform(input_df[features])
            is_eligible = classifier.predict(input_scaled_clf)[0]
            probability = classifier.predict_proba(input_scaled_clf)[0, 1]
            
            claim_amount = 0.0
            if is_eligible == 1:
                input_scaled_reg = scaler_reg.transform(input_df[features])
                claim_amount = regressor.predict(input_scaled_reg)[0]
                claim_amount = round(float(claim_amount), 2)
            
            results.append({
                'index': i,
                'eligible': bool(is_eligible),
                'confidence': float(probability),
                'claim_amount': claim_amount
            })
        
        return jsonify({
            'status': 'success',
            'total': len(records),
            'results': results
        })
    
    except Exception as e:
        return jsonify({'status': 'error', 'message': str(e)}), 500

@app.route('/info', methods=['GET'])
def model_info():
    """Get model information"""
    return jsonify({
        'model_type': 'OLD_MODEL',
        'features': features,
        'classifier': str(type(classifier).__name__),
        'regressor': str(type(regressor).__name__),
        'files_used': [
            'insurance_classifier.pkl',
            'claim_amount_regressor.pkl',
            'classifier_scaler.pkl',
            'regressor_scaler.pkl'
        ]
    })

# ============================================
# START THE SERVER
# ============================================

if __name__ == '__main__':
    import socket
    
    print("\n" + "="*60)
    print("🎯 INSURANCE PREDICTION API (OLD MODEL)")
    print("="*60)
    
    # Get your IP addresses
    hostname = socket.gethostname()
    
    # Get Radmin IP or local IP
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        local_ip = s.getsockname()[0]
        s.close()
    except:
        local_ip = "Unable to detect"
    
    print(f"\n📍 CONNECTION DETAILS FOR JAVA FRIEND:")
    print(f"   Method 1 (Radmin VPN): http://[YOUR_RADMIN_IP]:5000/predict")
    print(f"   Method 2 (Local IP): http://{local_ip}:5000/predict")
    print(f"   Method 3 (Localhost): http://127.0.0.1:5000/predict (test only)")
    
    print(f"\n🔗 TEST ENDPOINTS:")
    print(f"   Health check: http://127.0.0.1:5000/health")
    print(f"   Model info: http://127.0.0.1:5000/info")
    print(f"   Predict: POST to http://127.0.0.1:5000/predict")
    
    print("\n" + "="*60)
    print("🚀 Server starting... Press CTRL+C to stop\n")
    
    # Run on all network interfaces
    app.run(host='0.0.0.0', port=5000, debug=True)
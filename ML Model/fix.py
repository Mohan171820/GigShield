import joblib
import pickle

files = ['insurance_classifier.pkl', 'claim_amount_regressor.pkl', 'classifier_scaler.pkl', 'regressor_scaler.pkl']

for f in files:
    print(f'Processing {f}...')
    model = joblib.load(f)
    with open(f, 'wb') as out:
        pickle.dump(model, out, protocol=4)
    print(f'Done: {f}')
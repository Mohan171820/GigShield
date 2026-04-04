FROM python:3.12-slim

WORKDIR /app

RUN apt-get update && apt-get install -y \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .

# Install your exact NumPy version
RUN pip install --no-cache-dir numpy==2.4.1
RUN pip install --no-cache-dir scikit-learn==1.3.0 pandas==2.0.3 flask==2.3.0 flask-cors==4.0.0 joblib==1.3.2 gunicorn==21.2.0

COPY *.pkl .
COPY api_server.py .

CMD ["gunicorn", "api_server:app", "--bind", "0.0.0.0:10000"]

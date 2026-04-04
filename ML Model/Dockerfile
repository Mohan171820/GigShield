FROM python:3.11-slim

WORKDIR /app

# Install gcc and build tools (required for scikit-learn)
RUN apt-get update && apt-get install -y \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Copy and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy model files and code
COPY *.pkl .
COPY api_server.py .

# Run the server
CMD ["gunicorn", "api_server:app", "--bind", "0.0.0.0:10000"]

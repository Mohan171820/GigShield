FROM python:3.12-slim

WORKDIR /app

# Copy requirements first (for caching)
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy all your model files and code
COPY *.pkl .
COPY api_server.py .

# Run the server
CMD ["gunicorn", "api_server:app", "--bind", "0.0.0.0:10000"]

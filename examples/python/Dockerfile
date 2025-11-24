FROM python:3.11-slim

WORKDIR /app

COPY sdks/python/workersdk ./workersdk

COPY examples/python/requirements.txt .
COPY examples/python/main.py .

RUN pip install --no-cache-dir -r requirements.txt

CMD ["python", "main.py"]

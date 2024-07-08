FROM python:3.11.7 AS builder
WORKDIR /app

RUN pip install pyinstaller

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY src/ /app/

ENV PYTHONPATH /app/:$PYTHONPATH

RUN pyinstaller --onefile app.py

FROM debian:stable-slim

WORKDIR /app

RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

    COPY --from=builder /app/dist/app app

EXPOSE 5001

CMD ["./app"]

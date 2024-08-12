FROM python:3.11.7 AS builder
WORKDIR /app

COPY requirements.txt .
COPY app/ /app/

RUN apt-get update && \
    apt-get install -y build-essential python3-dev libssl-dev libffi-dev curl wget && \
    rm -rf /var/lib/apt/lists/*

RUN pip install pyinstaller
RUN pip install --no-cache-dir -r requirements.txt

ENV PYTHONPATH /app/:$PYTHONPATH

RUN pyinstaller --onefile main.py

FROM debian:stable-slim

WORKDIR /app

RUN apt-get update && \
    rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/dist/main .

EXPOSE 8000

#ENTRYPOINT [ "sh", "-c", "fastapi run main.py" ]
ENTRYPOINT [ "./main" ]

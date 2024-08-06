from fastapi import FastAPI, HTTPException
from contextlib import asynccontextmanager

import asyncio
from app.recommender import generate_recommendations
from app.kafka_consumer import consume_events
from app.utils import initialize_opensearch

app = FastAPI()


@asynccontextmanager
async def startup_event():
    # Initialize OpenSearch
    initialize_opensearch()

    # Start Kafka consumer
    asyncio.create_task(consume_events())



@app.get("/")
async def root():
    return {"message": "Welcome to the Recommendation Service"}


@app.get("/recommend/{user_id}")
async def recommend(user_id: str):
    try:
        recommendations = await generate_recommendations(user_id)
        return {"user_id": user_id, "recommendations": recommendations}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

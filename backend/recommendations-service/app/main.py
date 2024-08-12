import uvicorn
from contextlib import asynccontextmanager
from fastapi import FastAPI, HTTPException

import asyncio
from recommender import generate_recommendations
from kafka_consumer import consume_events
from utils import initialize_opensearch


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Initialize OpenSearch
    initialize_opensearch()

    # Start Kafka consumer
    asyncio.create_task(consume_events())

    yield  # Lines before yield are executed on startup and lines after yield are executed after shutdown


app = FastAPI(lifespan=lifespan)


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


if __name__ == '__main__':
    uvicorn.run(app, host="0.0.0.0", port=8000)

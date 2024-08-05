import asyncio
from confluent_kafka import Consumer
import json
import logging
from .models import UserEvent
from .recommender import update_user_events
from .config import settings

logging.basicConfig(level=logging.INFO)


async def consume_events():
    loop = asyncio.get_event_loop()

    # Configure the Kafka consumer
    consumer = Consumer(
        {
            "bootstrap.servers": settings.kafka_bootstrap_servers,
            "group.id": settings.kafka_group_id,
            "auto.offset.reset": "earliest",
        }
    )
    consumer.subscribe([settings.kafka_topic])

    try:
        while True:
            msg = consumer.poll(1.0)  # Poll for new messages
            if msg is None:
                await asyncio.sleep(1)
                continue
            if msg.error():
                logging.error(f"Consumer error: {msg.error()}")
                continue

            # Parse the message
            event_data = json.loads(msg.value().decode("utf-8"))
            event = UserEvent(**event_data)
            loop.create_task(process_event(event))

    finally:
        consumer.close()


async def process_event(event: UserEvent):
    # Asynchronously process and store the event
    update_user_events(event)
    logging.info(f"Processed event for user {event.user_id}")

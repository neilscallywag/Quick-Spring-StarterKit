import asyncio
from datetime import datetime
from types import NoneType
from typing import get_args, Type

from confluent_kafka import Consumer
import json
import logging
from models import UserEvent, BaseModel
from recommender import update_user_events
from config import settings

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
            event_data = validate_event_data(event_data, UserEvent)

            event = UserEvent(**event_data)
            loop.create_task(process_event(event))

    finally:
        consumer.close()


def validate_event_data(event_data: dict, model: Type[BaseModel]):
    for key, value in event_data.items():
        optional = False

        try:
            # TODO: use field matching to determine which type to use if multiple exist
            expected_type = get_args(model.model_fields[key].annotation)[0]
            optional = True
        except Exception:
            expected_type = model.model_fields[key].annotation

        if isinstance(value, expected_type):
            continue

        # logging.info(
        #     f"Key: {key}, Value: {value}, Type: {type(value)}, Expected Type: {expected_type}"
        # )

        if isinstance(value, dict):
            event_data[key] = expected_type(**validate_event_data(value, expected_type))
        elif isinstance(value, list) and expected_type == str:
            try:
                event_data[key] = datetime(*value).strftime('%Y-%m-%d %H:%M:%S')
            except Exception:
                event_data[key] = str(value)
        elif optional and isinstance(value, NoneType):
            continue
        else:
            event_data[key] = expected_type(value)

    # logging.info(f"{event_data.__str__()}")
    return event_data


async def process_event(event: UserEvent):
    # Asynchronously process and store the event
    update_user_events(event)
    logging.info(f"Processed event for user {event.user_id}")

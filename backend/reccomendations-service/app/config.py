from pydantic import BaseSettings


class Settings(BaseSettings):
    kafka_bootstrap_servers: str = "kafka:9092"
    kafka_group_id: str = "recommendation-group"
    kafka_topic: str = "job_events"
    opensearch_host: str = "http://opensearch:9200"
    opensearch_index: str = "user_recommendations"
    db_url: str = "postgresql+asyncpg://user:password@postgres/dbname"
    num_clusters: int = 3  # Number of clusters for user segmentation


settings = Settings()

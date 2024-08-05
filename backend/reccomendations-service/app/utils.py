from opensearchpy import OpenSearch
from .config import settings

# Create an OpenSearch client
client = OpenSearch(
    hosts=[settings.opensearch_host],
    http_compress=True,
    timeout=30,
    max_retries=10,
    retry_on_timeout=True
)

# Initialize OpenSearch index
def initialize_opensearch():
    if not client.indices.exists(index=settings.opensearch_index):
        client.indices.create(index=settings.opensearch_index, body={
            "settings": {
                "index": {
                    "number_of_shards": 1,
                    "number_of_replicas": 1
                }
            },
            "mappings": {
                "properties": {
                    "user_id": {"type": "keyword"},
                    "recommended_jobs": {"type": "keyword"},
                    "timestamp": {"type": "date"}
                }
            }
        })


def store_recommendations_in_opensearch(user_id: str, recommendations: list):
    client.index(index=settings.opensearch_index, body={
        "user_id": user_id,
        "recommended_jobs": recommendations,
        "timestamp": "now"
    })


def get_recommendations_from_opensearch(user_id: str):
    response = client.search(index=settings.opensearch_index, body={
        "query": {
            "match": {
                "user_id": user_id
            }
        },
        "sort": [
            {"timestamp": "desc"}
        ],
        "size": 1
    })
    hits = response['hits']['hits']
    if hits:
        return hits[0]['_source']['recommended_jobs']
    return None

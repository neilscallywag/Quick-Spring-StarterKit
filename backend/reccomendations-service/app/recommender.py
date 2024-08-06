import logging
from collections import defaultdict
from typing import List
from sklearn.cluster import KMeans
import numpy as np
from app.models import UserEvent
from app.utils import store_recommendations_in_opensearch, get_recommendations_from_opensearch
from app.config import settings

user_events = defaultdict(list)
logging.basicConfig(level=logging.INFO)


def update_user_events(event: UserEvent):
    user_events[event.user_id].append(event)


async def generate_recommendations(user_id: str) -> List[str]:
    # Check OpenSearch cache first
    cached_recommendations = get_recommendations_from_opensearch(user_id)
    if cached_recommendations:
        return cached_recommendations

    if user_id not in user_events:
        return []  # Return default items if no history

    # Simple recommendation logic based on user events
    job_ids = [event.job_details.job_id for event in user_events[user_id] if event.job_details]

    # Cluster users to recommend similar jobs
    cluster_recommendations = await cluster_based_recommendations()

    recommendations = list(set(job_ids + cluster_recommendations.get(user_id, [])))

    # Store recommendations in OpenSearch
    store_recommendations_in_opensearch(user_id, recommendations)

    return recommendations


async def cluster_based_recommendations():
    job_ids = list(set(job_id for events in user_events.values() for event in events if event.job_details for job_id in [event.job_details.job_id]))
    job_index = {job_id: idx for idx, job_id in enumerate(job_ids)}

    user_features = np.zeros((len(user_events), len(job_ids)))
    user_index = {}
    for i, (user_id, events) in enumerate(user_events.items()):
        user_index[user_id] = i
        for event in events:
            if event.job_details:
                job_id = event.job_details.job_id
                user_features[i, job_index[job_id]] = 1

    # Perform clustering
    n_clusters = settings.num_clusters
    kmeans = KMeans(n_clusters=n_clusters, random_state=0)
    kmeans.fit(user_features)

    # Assign users to clusters and generate recommendations
    user_clusters = kmeans.predict(user_features)
    cluster_recommendations = defaultdict(list)
    for cluster in range(n_clusters):
        cluster_users = [user_id for user_id, idx in user_index.items() if user_clusters[idx] == cluster]
        cluster_jobs = [job_id for user_id in cluster_users for event in user_events[user_id] if event.job_details for job_id in [event.job_details.job_id]]
        cluster_recommendations.update({user_id: recommend_top_jobs(cluster_jobs) for user_id in cluster_users})

    return cluster_recommendations


def recommend_top_jobs(jobs):
    job_count = defaultdict(int)
    for job_id in jobs:
        job_count[job_id] += 1
    sorted_jobs = sorted(job_count, key=job_count.get, reverse=True)
    return sorted_jobs[:3]  # Recommend top 3 jobs

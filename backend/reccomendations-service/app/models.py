from typing import Optional
from pydantic import BaseModel


class JobDetails(BaseModel):
    job_title: str
    job_id: str
    company_name: str
    location: Optional[str] = None
    job_type: Optional[str] = None
    industry: Optional[str] = None
    salary_range: Optional[str] = None
    posted_date: Optional[str] = None


class UserEvent(BaseModel):
    event_datetime: str
    event: str
    user_id: str
    client_id: str
    page: str
    page_previous: str
    job_details: Optional[JobDetails] = None
    application: Optional[dict] = None
    offer_details: Optional[dict] = None
    profile_update: Optional[dict] = None
    company_details: Optional[dict] = None

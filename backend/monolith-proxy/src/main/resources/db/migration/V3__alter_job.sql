-- Add job_description column to jobs table
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS job_description TEXT;

-- Create the job_keywords table if it doesn't exist
CREATE TABLE IF NOT EXISTS job_keywords (
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    keyword VARCHAR(255) NOT NULL
);

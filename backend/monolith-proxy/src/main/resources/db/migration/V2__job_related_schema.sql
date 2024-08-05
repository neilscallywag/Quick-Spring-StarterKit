-- Create the companies table if it doesn't exist
CREATE TABLE IF NOT EXISTS companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_name VARCHAR(255) NOT NULL UNIQUE,
    industry VARCHAR(255),
    location VARCHAR(255),
    size VARCHAR(255)
);

-- Create the jobs table if it doesn't exist
CREATE TABLE IF NOT EXISTS jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_title VARCHAR(255) NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    job_type VARCHAR(255),
    industry VARCHAR(255),
    salary_range VARCHAR(255),
    posted_date TIMESTAMP NOT NULL,
    FOREIGN KEY (company_name) REFERENCES companies(company_name) ON DELETE CASCADE
);

-- Create the applications table if it doesn't exist
CREATE TABLE IF NOT EXISTS applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    resume_id VARCHAR(255) NOT NULL,
    cover_letter_id VARCHAR(255),
    application_status VARCHAR(255) NOT NULL
);

-- Create the offers table if it doesn't exist
CREATE TABLE IF NOT EXISTS offers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    salary_offered FLOAT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    offer_status VARCHAR(255) NOT NULL
);

-- Create the profile_updates table if it doesn't exist
CREATE TABLE IF NOT EXISTS profile_updates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    timestamp TIMESTAMP NOT NULL
);

-- Create the profile_update_fields table if it doesn't exist
CREATE TABLE IF NOT EXISTS profile_update_fields (
    profile_update_id UUID NOT NULL REFERENCES profile_updates(id) ON DELETE CASCADE,
    field_updated VARCHAR(255) NOT NULL
);

CREATE TABLE job_applications (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(150) NOT NULL,
    job_title VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL,
    applied_at TIMESTAMP NOT NULL
);
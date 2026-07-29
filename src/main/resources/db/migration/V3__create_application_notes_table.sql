CREATE TABLE application_notes (
    id BIGSERIAL PRIMARY KEY,
    content VARCHAR(500) NOT NULL,
    job_application_id BIGINT NOT NULL,

    CONSTRAINT fk_application_notes_job_application
        FOREIGN KEY (job_application_id)
        REFERENCES job_applications(id)
        ON DELETE CASCADE
);
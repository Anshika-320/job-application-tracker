ALTER TABLE job_applications
    RENAME COLUMN job_title TO role;

ALTER TABLE job_applications
    DROP COLUMN applied_at;

ALTER TABLE job_applications
    ADD COLUMN location VARCHAR(150);
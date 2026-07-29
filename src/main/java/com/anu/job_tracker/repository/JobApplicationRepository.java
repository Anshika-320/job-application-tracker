package com.anu.job_tracker.repository;

import com.anu.job_tracker.ApplicationStatus;
import com.anu.job_tracker.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByStatus(ApplicationStatus status);
    List<JobApplication> findByCompanyNameContainingIgnoreCase(
            String companyName
    );
    @Query("""
       SELECT application
       FROM JobApplication application
       WHERE LOWER(application.location) = LOWER(:location)
       """)
    List<JobApplication> findApplicationsByLocation(
            @Param("location") String location
    );
    @Query(
            value = """
                SELECT *
                FROM job_applications
                WHERE status = :status
                """,
            nativeQuery = true
    )
    List<JobApplication> findApplicationsByStatusNative(
            @Param("status") String status
    );
    @Query("""
        SELECT DISTINCT application
        FROM JobApplication application
        LEFT JOIN FETCH application.notes
        """)
    List<JobApplication> findAllWithNotes();
}

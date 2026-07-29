package com.anu.job_tracker.repository;

import com.anu.job_tracker.entity.ApplicationNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


    public interface ApplicationNoteRepository
            extends JpaRepository<ApplicationNote, Long> {

        List<ApplicationNote> findByJobApplicationId(Long applicationId);
    }

package com.anu.job_tracker.service;

import com.anu.job_tracker.dto.ApplicationNoteRequest;
import com.anu.job_tracker.dto.ApplicationNoteResponse;
import com.anu.job_tracker.entity.ApplicationNote;
import com.anu.job_tracker.entity.JobApplication;
import com.anu.job_tracker.exception.ResourceNotFoundException;
import com.anu.job_tracker.repository.ApplicationNoteRepository;
import com.anu.job_tracker.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

    @Service
    public class ApplicationNoteService {

        private final ApplicationNoteRepository noteRepository;
        private final JobApplicationRepository jobApplicationRepository;

        public ApplicationNoteService(
                ApplicationNoteRepository noteRepository,
                JobApplicationRepository jobApplicationRepository
        ) {
            this.noteRepository = noteRepository;
            this.jobApplicationRepository = jobApplicationRepository;
        }

        @Transactional
        public ApplicationNoteResponse addNote(
                Long applicationId,
                ApplicationNoteRequest request
        ) {
            JobApplication application =
                    jobApplicationRepository.findById(applicationId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Job application not found with id: "
                                                    + applicationId
                                    )
                            );

            ApplicationNote note = new ApplicationNote();
            note.setContent(request.getContent());
            note.setJobApplication(application);

            ApplicationNote savedNote = noteRepository.save(note);
            return convertToResponse(savedNote);
        }

        @Transactional(readOnly = true)
        public List<ApplicationNoteResponse> getNotes(Long applicationId) {
            if (!jobApplicationRepository.existsById(applicationId)) {
                throw new ResourceNotFoundException(
                        "Job application not found with id: " + applicationId
                );
            }

            return noteRepository.findByJobApplicationId(applicationId)
                    .stream()
                    .map(this::convertToResponse)
                    .toList();
        }
        @Transactional
        public void deleteNote(Long applicationId, Long noteId) {
            JobApplication application =
                    jobApplicationRepository.findById(applicationId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Job application not found with id: "
                                                    + applicationId
                                    )
                            );

            ApplicationNote note = application.getNotes()
                    .stream()
                    .filter(existingNote ->
                            existingNote.getId().equals(noteId)
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Note not found with id: " + noteId
                            )
                    );

            application.removeNote(note);
        }

        private ApplicationNoteResponse convertToResponse(
                ApplicationNote note
        ) {
            return new ApplicationNoteResponse(
                    note.getId(),
                    note.getContent(),
                    note.getJobApplication().getId()
            );
        }
    }

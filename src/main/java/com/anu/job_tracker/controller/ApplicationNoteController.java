package com.anu.job_tracker.controller;

import com.anu.job_tracker.dto.ApplicationNoteRequest;
import com.anu.job_tracker.dto.ApplicationNoteResponse;
import com.anu.job_tracker.service.ApplicationNoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    @RestController
    @RequestMapping("/api/applications/{applicationId}/notes")
    public class ApplicationNoteController {

        private final ApplicationNoteService noteService;

        public ApplicationNoteController(ApplicationNoteService noteService) {
            this.noteService = noteService;
        }

        @PostMapping
        public ResponseEntity<ApplicationNoteResponse> addNote(
                @PathVariable Long applicationId,
                @RequestBody ApplicationNoteRequest request
        ) {
            ApplicationNoteResponse response =
                    noteService.addNote(applicationId, request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        }

        @GetMapping
        public ResponseEntity<List<ApplicationNoteResponse>> getNotes(
                @PathVariable Long applicationId
        ) {
            return ResponseEntity.ok(
                    noteService.getNotes(applicationId)
            );
        }
        @DeleteMapping("/{noteId}")
        public ResponseEntity<Void> deleteNote(
                @PathVariable Long applicationId,
                @PathVariable Long noteId
        ) {
            noteService.deleteNote(applicationId, noteId);
            return ResponseEntity.noContent().build();
        }
    }


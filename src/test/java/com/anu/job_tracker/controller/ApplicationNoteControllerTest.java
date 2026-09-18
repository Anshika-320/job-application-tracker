package com.anu.job_tracker.controller;

import com.anu.job_tracker.dto.ApplicationNoteResponse;
import com.anu.job_tracker.security.CustomUserDetailsService;
import com.anu.job_tracker.security.JwtService;
import com.anu.job_tracker.service.ApplicationNoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ApplicationNoteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplicationNoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApplicationNoteService noteService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void rejectsBlankNoteContent() throws Exception {
        mockMvc.perform(post("/api/applications/1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").value("Note content is required"));

        verify(noteService, never()).addNote(any(), any());
    }

    @Test
    void rejectsMissingNoteContent() throws Exception {
        mockMvc.perform(post("/api/applications/1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").value("Note content is required"));

        verify(noteService, never()).addNote(any(), any());
    }

    @Test
    void rejectsNoteContentLongerThanTheColumnAllows() throws Exception {
        String tooLong = "a".repeat(501);

        mockMvc.perform(post("/api/applications/1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"" + tooLong + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content")
                        .value("Note content must be 500 characters or fewer"));

        verify(noteService, never()).addNote(any(), any());
    }

    @Test
    void createsValidNote() throws Exception {
        when(noteService.addNote(eq(1L), ArgumentMatchers.any()))
                .thenReturn(new ApplicationNoteResponse(7L, "Recruiter call booked", 1L));

        mockMvc.perform(post("/api/applications/1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Recruiter call booked\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.content").value("Recruiter call booked"))
                .andExpect(jsonPath("$.jobApplicationId").value(1));
    }
}

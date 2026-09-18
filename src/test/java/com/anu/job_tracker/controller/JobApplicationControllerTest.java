package com.anu.job_tracker.controller;

import com.anu.job_tracker.dto.JobApplicationResponse;
import com.anu.job_tracker.security.CustomUserDetailsService;
import com.anu.job_tracker.security.JwtService;
import com.anu.job_tracker.service.JobApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = JobApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobApplicationService jobApplicationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void rejectsMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyName\":\"\",\"role\":\"\",\"status\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.companyName").value("Company name is required"))
                .andExpect(jsonPath("$.role").value("Role is required"))
                .andExpect(jsonPath("$.status").value("Status is required"));

        verify(jobApplicationService, never()).createApplication(any());
    }

    @Test
    void rejectsValuesLongerThanTheColumnAllows() throws Exception {
        String tooLong = "a".repeat(151);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyName\":\"" + tooLong + "\","
                                + "\"role\":\"" + tooLong + "\","
                                + "\"status\":\"APPLIED\","
                                + "\"location\":\"" + tooLong + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.companyName")
                        .value("Company name must be 150 characters or fewer"))
                .andExpect(jsonPath("$.role").value("Role must be 150 characters or fewer"))
                .andExpect(jsonPath("$.location")
                        .value("Location must be 150 characters or fewer"));

        verify(jobApplicationService, never()).createApplication(any());
    }

    @Test
    void reportsUnknownStatusAsBadRequest() throws Exception {
        when(jobApplicationService.createApplication(any()))
                .thenThrow(new IllegalArgumentException("No enum constant NOT_A_STATUS"));

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyName\":\"Acme\",\"role\":\"Engineer\","
                                + "\"status\":\"NOT_A_STATUS\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createsValidApplication() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 9, 18, 10, 0);
        when(jobApplicationService.createApplication(any()))
                .thenReturn(new JobApplicationResponse(
                        3L, "Acme", "Engineer", "APPLIED", "Remote", now, now));

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyName\":\"Acme\",\"role\":\"Engineer\","
                                + "\"status\":\"APPLIED\",\"location\":\"Remote\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.companyName").value("Acme"))
                .andExpect(jsonPath("$.status").value("APPLIED"))
                .andExpect(jsonPath("$.createdAt").exists());
    }
}

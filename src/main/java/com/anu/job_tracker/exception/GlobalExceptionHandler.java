package com.anu.job_tracker.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
            Map<String, Object> error = new HashMap<>();

            error.put("timestamp", LocalDateTime.now());
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, Object>> handleIllegalArgument(
                IllegalArgumentException ex
        ) {
            Map<String, Object> error = new HashMap<>();

            error.put("timestamp", LocalDateTime.now());
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put(
                    "message",
                    "Invalid application status. Use APPLIED, INTERVIEW_SCHEDULED, " +
                            "INTERVIEWED, OFFERED, REJECTED, or WITHDRAWN."
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
            Map<String, Object> errors = new HashMap<>();

            ex.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
    }


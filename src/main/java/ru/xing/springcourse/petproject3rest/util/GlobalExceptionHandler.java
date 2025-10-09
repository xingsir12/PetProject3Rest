package ru.xing.springcourse.petproject3rest.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeExceptionHandler(RuntimeException e) {
        log.error("Error: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(Map.of("error", e.getMessage()));
    }
}

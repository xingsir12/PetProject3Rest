package ru.xing.springcourse.petproject3rest.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    //Обработка MeasurementException.
    //Возвращает тело ошибки в формате json.
    @ExceptionHandler(MeasurementException.class)
    public ResponseEntity<Object> handleRuntimeExceptionHandler(MeasurementException e) {
        Map<String, Object> body = Map.of(
                "message", "Validation failed",
                "errors", e.getErrors(),
                "Timestamp" , System.currentTimeMillis());

        return ResponseEntity.badRequest().body(body);
    }

    //Обработка Runtime exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeExceptionHandler(RuntimeException e) {
        log.error("Runtime exception: ", e);
        Map<String, Object> body = Map.of(
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessExceptionHandler(BusinessException e) {
        log.error("Business exception: {}", e.getMessage());
        Map<String, Object> body = Map.of(
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
        );

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage());
        Map<String, Object> body = Map.of(
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

package com.example.demo_job.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@ControllerAdvice
public class ScrapingExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientException(WebClientResponseException ex) {
        log.error("WebClient error: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
            ex.getRawStatusCode(),
            "Error fetching external data: " + ex.getMessage(),
            System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(ScrapingException.class)
    public ResponseEntity<ErrorResponse> handleScrapingException(ScrapingException ex) {
        log.error("Scraping error: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error processing external data: " + ex.getMessage(),
            System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


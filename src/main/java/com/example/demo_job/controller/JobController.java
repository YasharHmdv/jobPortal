package com.example.demo_job.controller;

import com.example.demo_job.service.DjinniScraperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    
    private final DjinniScraperService djinniScraperService;
    
    public JobController(DjinniScraperService djinniScraperService) {
        this.djinniScraperService = djinniScraperService;
    }
    
    @PostMapping("/scrape-djinni")
    public ResponseEntity<String> scrapeDjinniJobs() {
        try {
            int scrapedCount = djinniScraperService.scrapeAndSaveJobs();
            return ResponseEntity.ok("Successfully scraped and saved " + scrapedCount + " jobs from Djinni.co");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error scraping jobs: " + e.getMessage());
        }
    }
}
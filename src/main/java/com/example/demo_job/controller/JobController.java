package com.example.demo_job.controller;

import com.example.demo_job.model.JobListing;
import com.example.demo_job.service.DjinniAuthService;
import com.example.demo_job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jobs")
public class JobController {
    private final JobLauncher jobLauncher;
    private final Job scrapeDjinniJobsJob;
    private final JobService jobService;
    private final DjinniAuthService authService;
    @PostMapping("/load")
    public ResponseEntity<Map<String, Object>> loadJobs() throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException,
            JobRestartException,
            JobInstanceAlreadyCompleteException, IOException {

        // Authenticate with Djinni first
        authService.authenticate();

        Map<String, JobParameter> params = new HashMap<>();
        params.put("startTime", new JobParameter(System.currentTimeMillis()));

        JobExecution execution = jobLauncher.run(
                scrapeDjinniJobsJob,
                new JobParameters(params)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("jobId", execution.getJobId());
        response.put("status", execution.getStatus());
        response.put("startTime", execution.getStartTime());

        return ResponseEntity.ok(response);
    }

    /**
     * Get all jobs with pagination
     */
    @GetMapping
    public ResponseEntity<Page<JobListing>> getAllJobs(Pageable pageable) {
        return ResponseEntity.ok(jobService.getAllJobs(pageable));
    }

    /**
     * Filter jobs by various criteria
     */
    @GetMapping("/filter")
    public ResponseEntity<Page<JobListing>> filterJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {

        return ResponseEntity.ok(jobService.filterJobs(
                location, jobType, experienceLevel, industry, keyword, pageable
        ));
    }

    /**
     * Get job by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobListing> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search jobs by keyword in title/description
     */
    @GetMapping("/search")
    public ResponseEntity<Page<JobListing>> searchJobs(
            @RequestParam String query,
            Pageable pageable) {
        return ResponseEntity.ok(jobService.searchJobs(query, pageable));
    }

}

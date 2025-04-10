package com.example.demo_job.controller;

import com.example.demo_job.config.JobMapper;
import com.example.demo_job.dtos.*;
import com.example.demo_job.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jobs")
public class JobController {

    private final JobMapper jobMapper;
    private final JobService jobService;
    @PostMapping
    public ResponseEntity<JobDto> createJob(@Valid @RequestBody JobPostDto jobPostDto) {
        JobDto createdJob = jobService.createJob(jobPostDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
    }
    @GetMapping("/search")
    public ResponseEntity<JobResponse> searchJobs(
            @Valid JobSearchRequest searchRequest,
            @PageableDefault(sort = "postedDate", direction = Sort.Direction.DESC) Pageable pageable) {

        JobSearchCriteria criteria = jobMapper.toSearchCriteria(searchRequest);
        Page<JobDto> jobs = jobService.getAllJobs(criteria, pageable);
        return ResponseEntity.ok(new JobResponse(jobs));
    }
    @PostMapping("/load")
    public ResponseEntity<BatchJobLoadResponse> loadJobs() {
        jobService.loadJobsFromExternalSource();
        return ResponseEntity.ok(new BatchJobLoadResponse(/*...*/));
    }


}

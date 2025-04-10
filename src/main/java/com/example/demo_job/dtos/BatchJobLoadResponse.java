package com.example.demo_job.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class BatchJobLoadResponse {
    private int jobsLoaded;
    private int jobsSkipped;
    private String message;
    
    public BatchJobLoadResponse(int jobsLoaded, int jobsSkipped) {
        this.jobsLoaded = jobsLoaded;
        this.jobsSkipped = jobsSkipped;
        this.message = String.format("Successfully loaded %d jobs (%d skipped)", jobsLoaded, jobsSkipped);
    }
}
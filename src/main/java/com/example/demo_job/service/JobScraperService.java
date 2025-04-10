package com.example.demo_job.service;

import com.example.demo_job.dtos.JobPostDto;

import java.util.List;

public interface JobScraperService {
    List<JobPostDto> scrapeDjinniJobs();
}
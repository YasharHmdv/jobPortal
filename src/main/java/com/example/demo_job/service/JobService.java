package com.example.demo_job.service;

import com.example.demo_job.dtos.JobDto;
import com.example.demo_job.dtos.JobPostDto;
import com.example.demo_job.dtos.JobSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobService {
    JobDto createJob(JobPostDto jobPostDto);
    Page<JobDto> getAllJobs(JobSearchCriteria criteria, Pageable pageable);
    JobDto getJobById(Long id);
    JobDto updateJob(Long id, JobPostDto jobPostDto);
    void deleteJob(Long id);
    void loadJobsFromExternalSource();
}
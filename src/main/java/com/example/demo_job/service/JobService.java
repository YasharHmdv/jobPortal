package com.example.demo_job.service;

import com.example.demo_job.model.JobListing;
import com.example.demo_job.repo.JobListingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobService {

    private final JobListingRepository jobListingRepository;

    public JobService(JobListingRepository jobListingRepository) {
        this.jobListingRepository = jobListingRepository;
    }

    public Page<JobListing> getAllJobs(Pageable pageable) {
        return jobListingRepository.findAll(pageable);
    }

    public Page<JobListing> filterJobs(String location, 
                                     String jobType, 
                                     String experienceLevel, 
                                     String industry, 
                                     String keyword,
                                     Pageable pageable) {
        // Implement your custom filtering logic
        // This could use JPA Specifications or QueryDSL
        return jobListingRepository.findByFilters(
            location, jobType, experienceLevel, industry, keyword, pageable
        );
    }

    public Optional<JobListing> getJobById(Long id) {
        return jobListingRepository.findById(id);
    }

    public Page<JobListing> searchJobs(String query, Pageable pageable) {
        return jobListingRepository.search(query, pageable);
    }
}
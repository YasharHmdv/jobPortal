package com.example.demo_job.service;

import com.example.demo_job.model.JobListing;
import com.example.demo_job.repo.JobListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class JobListingItemWriter implements ItemWriter<JobListing> {

    private final JobListingRepository jobListingRepository;


    @Override
    public void write(Chunk<? extends JobListing> chunk) throws Exception {
        jobListingRepository.saveAll(chunk);
    }
}
package com.example.demo_job.repo;

import com.example.demo_job.model.JobListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobListingRepository extends JpaRepository<JobListing,Long> {
}

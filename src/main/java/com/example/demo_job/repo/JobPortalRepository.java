package com.example.demo_job.repo;

import com.example.demo_job.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface JobPortalRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    // Custom queries will go here
}

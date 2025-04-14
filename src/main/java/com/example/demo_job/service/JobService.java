package com.example.demo_job.service;

import com.example.demo_job.model.Job;
import com.example.demo_job.model.JobFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface JobService {

    List<Job> getAllJobs();
    List<Job> filterJobs(JobFilter jobFilter)
}

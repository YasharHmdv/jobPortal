package com.example.demo_job.model;

import com.example.demo_job.model.enums.ExperienceLevel;
import com.example.demo_job.model.enums.JobType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JobFilter {
    private String location;
    private JobType jobType;
    private ExperienceLevel experienceLevel;
    private String industry;
    private String keyword;
}
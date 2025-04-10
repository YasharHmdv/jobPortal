package com.example.demo_job.dtos;

import com.example.demo_job.model.enums.ExperienceLevel;
import com.example.demo_job.model.enums.JobType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class JobSearchCriteria {
    private String location;
    private JobType jobType;
    private ExperienceLevel experienceLevel;
    private String industry;
    private Set<String> tags;
    private Boolean remote;
    private Boolean offersRelocation;
    private LocalDate postedAfter;
}
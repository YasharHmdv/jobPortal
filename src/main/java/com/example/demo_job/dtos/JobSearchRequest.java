package com.example.demo_job.dtos;

import com.example.demo_job.model.enums.ExperienceLevel;
import com.example.demo_job.model.enums.JobType;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class JobSearchRequest {
    private String location;
    private JobType jobType;
    private ExperienceLevel experienceLevel;
    private String industry;
    private Set<String> tags;
    private Boolean remote;
    private Boolean offersRelocation;
    private LocalDate postedAfter;
    private String sortBy; // "postedDate" or "salary"
    private String sortDirection; // "asc" or "desc"
}
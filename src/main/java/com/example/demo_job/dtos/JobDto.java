package com.example.demo_job.dtos;

import com.example.demo_job.model.enums.EducationLevel;
import com.example.demo_job.model.enums.ExperienceLevel;
import com.example.demo_job.model.enums.JobType;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class JobDto {
    private Long id;
    private String title;
    private String companyName;
    private LocationDto location;
    private JobType jobType;
    private SalaryRangeDto salaryRange;
    private String description;
    private String requirements;
    private ExperienceLevel experienceLevel;
    private EducationLevel educationLevel;
    private String industry;
    private LocalDate postedDate;
    private LocalDate applicationDeadline;
    private String howToApply;
    private String companyLogoUrl;
    private String benefits;
    private Set<String> tags;
    private String sourceUrl;
    private boolean remote;
    private Set<String> countries;
    private boolean offersRelocation;
}
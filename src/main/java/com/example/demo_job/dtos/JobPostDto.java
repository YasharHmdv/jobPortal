package com.example.demo_job.dtos;

import com.example.demo_job.model.enums.EducationLevel;
import com.example.demo_job.model.enums.ExperienceLevel;
import com.example.demo_job.model.enums.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class JobPostDto {
    @NotBlank(message = "Job title is required")
    private String title;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    private String companyWebsite;
    private String companyDescription;
    
    @NotNull(message = "Location is required")
    private LocationDto location;
    
    @NotNull(message = "Job type is required")
    private JobType jobType;
    
    private SalaryRangeDto salaryRange;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String requirements;
    private ExperienceLevel experienceLevel;
    private EducationLevel educationLevel;
    private String industry;
    
    @NotNull(message = "Posted date is required")
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
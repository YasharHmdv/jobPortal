package com.example.demo_job.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Data
@RequiredArgsConstructor
public class JobListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String jobTitle;
    private String companyName;
    private String location;
    private String jobType; // Full-time, Part-time, etc.
    private String salaryRange;
    private String jobDescription;
    private String requirements;
    private String experienceLevel; // Entry, Mid, Senior, Lead
    private String educationLevel;
    private String industry;
    private Date postedDate;
    private Date applicationDeadline;
    private String howToApply;
    private String companyLogoUrl;
    private String benefits;
    private String tags;
    private String source;
    private String originalLink;
}
package com.example.demo_job.model;

import com.example.demo_job.model.enums.EducationLevel;
import com.example.demo_job.model.enums.ExperienceLevel;
import com.example.demo_job.model.enums.JobType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Embedded
    private Location location;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;
    
    @Embedded
    private SalaryRange salaryRange;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String requirements;
    
    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;
    
    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id")
    private Industry industry;
    
    @Column(nullable = false)
    private LocalDate postedDate;
    
    private LocalDate applicationDeadline;
    
    @Column(columnDefinition = "TEXT")
    private String howToApply;
    
    private String companyLogoUrl;
    
    @Column(columnDefinition = "TEXT")
    private String benefits;
    
    @ElementCollection
    @CollectionTable(name = "job_tags", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();
    
    private String sourceUrl;
    
    private boolean remote;
    
    @ElementCollection
    @CollectionTable(name = "job_countries", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "country")
    private Set<String> countries = new HashSet<>();
    
    private boolean offersRelocation;
    
    // Constructors, getters, setters, equals, hashCode, toString
}
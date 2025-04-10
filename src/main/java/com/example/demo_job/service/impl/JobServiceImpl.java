package com.example.demo_job.service.impl;

import com.example.demo_job.config.JobMapper;
import com.example.demo_job.dtos.JobDto;
import com.example.demo_job.dtos.JobPostDto;
import com.example.demo_job.dtos.JobSearchCriteria;
import com.example.demo_job.exception.ResourceNotFoundException;
import com.example.demo_job.model.Company;
import com.example.demo_job.model.Industry;
import com.example.demo_job.model.Job;
import com.example.demo_job.repo.CompanyRepository;
import com.example.demo_job.repo.IndustryRepository;
import com.example.demo_job.repo.JobPortalRepository;
import com.example.demo_job.service.JobScraperService;
import com.example.demo_job.service.JobService;
import com.example.demo_job.service.JobSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobPortalRepository jobPortalRepository;
    private final CompanyRepository companyRepository;
    private final IndustryRepository industryRepository;
    private final JobMapper jobMapper;
    private final WebClient djinniWebClient; // For scraping
    private final JobScraperService jobScraperService;

    @Override
    @Transactional
    public JobDto createJob(JobPostDto jobPostDto) {
        Company company = companyRepository.findByName(jobPostDto.getCompanyName())
                .orElseGet(() -> companyRepository.save(
                        new Company(jobPostDto.getCompanyName(),
                                  jobPostDto.getCompanyWebsite(), 
                                  jobPostDto.getCompanyDescription())));

        Industry industry = null;
        if (jobPostDto.getIndustry() != null) {
            industry = industryRepository.findByName(jobPostDto.getIndustry())
                    .orElseGet(() -> industryRepository.save(new Industry(jobPostDto.getIndustry())));
        }

        Job job = jobMapper.toEntity(jobPostDto);
        job.setCompany(company);
        job.setIndustry(industry);
        job.setPostedDate(LocalDate.now());

        Job savedJob = jobPortalRepository.save(job);
        log.info("Created new job with ID: {}", savedJob.getId());
        return jobMapper.toDto(savedJob);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobDto> getAllJobs(JobSearchCriteria criteria, Pageable pageable) {
        Specification<Job> spec = JobSpecification.withCriteria(criteria);
        return jobPortalRepository.findAll(spec, pageable)
                .map(jobMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public JobDto getJobById(Long id) {
        Job job = jobPortalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        return jobMapper.toDto(job);
    }

    @Override
    @Transactional
    public JobDto updateJob(Long id, JobPostDto jobPostDto) {
        Job existingJob = jobPortalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        jobMapper.updateEntityFromDto(jobPostDto, existingJob);
        
        if (!existingJob.getCompany().getName().equals(jobPostDto.getCompanyName())) {
            Company company = companyRepository.findByName(jobPostDto.getCompanyName())
                    .orElseGet(() -> companyRepository.save(
                            new Company(jobPostDto.getCompanyName())));
            existingJob.setCompany(company);
        }

        Job updatedJob = jobPortalRepository.save(existingJob);
        log.info("Updated job with ID: {}", id);
        return jobMapper.toDto(updatedJob);
    }

    @Override
    @Transactional
    public void deleteJob(Long id) {
        if (!jobPortalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Job not found with id: " + id);
        }
        jobPortalRepository.deleteById(id);
        log.info("Deleted job with ID: {}", id);
    }

    @Override
    @Transactional
    public void loadJobsFromExternalSource() {
        List<JobPostDto> scrapedJobs = jobScraperService.scrapeDjinniJobs();

        List<Job> jobsToSave = scrapedJobs.stream()
                .filter(this::isValidJob)
                .map(jobMapper::toEntity)
                .collect(Collectors.toList());

        jobPortalRepository.saveAll(jobsToSave);
    }

    private boolean isValidJob(JobPostDto job) {
        // Filter according to requirements: Remote/Worldwide or includes Azerbaijan or offers relocation
        return job.isRemote() ||
                (job.getCountries() != null && job.getCountries().contains("Azerbaijan")) ||
                job.isOffersRelocation();
    }


    private List<Job> scrapeDjinniJobs() {
        // Implementation using WebClient or other HTTP client
        // This would call Djinni.co and parse the response
        return Collections.emptyList(); // Actual implementation needed
    }

    private List<Job> filterAndTransformJobs(List<Job> jobs) {
        return jobs.stream()
                .filter(job -> job.isRemote() || 
                        (job.getCountries() != null && 
                         job.getCountries().contains("Azerbaijan")) ||
                        job.isOffersRelocation())
                .filter(job -> job.getPostedDate().isAfter(LocalDate.now().minusMonths(3)))
                .collect(Collectors.toList());
    }
}
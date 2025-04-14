package com.example.demo_job.service;

import com.example.demo_job.model.Job;
import com.example.demo_job.model.JobFilter;
import com.example.demo_job.repo.CompanyRepository;
import com.example.demo_job.repo.CustomJobRepository;
import com.example.demo_job.repo.IndustryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final CustomJobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final IndustryRepository industryRepository;
    
    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
    
    @Override
    public List<Job> filterJobs(JobFilter filter) {
        Specification<Job> spec = Specification.where(null);
        
        if (filter.getLocation() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(root.get("location").get("country"), "%" + filter.getLocation() + "%"),
                    cb.like(root.get("location").get("city"), "%" + filter.getLocation() + "%"),
                    cb.equal(root.get("location").get("remote"), true)
                ));
        }
        
        if (filter.getJobType() != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("jobType"), filter.getJobType()));
        }
        
        // Add other filters similarly...
        
        return jobRepository.findAll(spec);
    }
    
    @Override
    public void saveAllJobs(List<Job> jobs) {
        jobRepository.saveAll(jobs);
    }
}
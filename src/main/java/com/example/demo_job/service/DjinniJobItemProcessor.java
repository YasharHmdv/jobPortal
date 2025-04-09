package com.example.demo_job.service;

import com.example.demo_job.model.DjinniJobItem;
import com.example.demo_job.model.JobListing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ParseException;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DjinniJobItemProcessor implements ItemProcessor<DjinniJobItem, JobListing> {
    
    @Override
    public JobListing process(DjinniJobItem item) throws Exception {
        JobListing jobListing = new JobListing();
        
        // Transform Djinni-specific format to your standard format
        jobListing.setJobTitle(item.getTitle());
        jobListing.setCompanyName(item.getCompany());
        jobListing.setLocation(item.getLocation());
        
        // Map Djinni job types to your standard types
        jobListing.setJobType(mapJobType(item.getJobType()));
        
        // Set other fields
        jobListing.setPostedDate(parseDate(item.getPostedDate()));
        jobListing.setSource("Djinni.co");
        jobListing.setOriginalLink(item.getJobUrl());
        
        return jobListing;
    }
    
    private String mapJobType(String djinniType) {
        // Implement mapping logic
        return switch (djinniType.toLowerCase()) {
            case "full remote" -> "Full-time";
            case "part-time" -> "Part-time";
            default -> "Contract";
        };
    }
    
    private Date parseDate(String dateString) {
        // Implement date parsing
        try {
            return new SimpleDateFormat("dd MMM yyyy").parse(dateString);
        } catch (ParseException | java.text.ParseException e) {
            return new Date(); // fallback to current date
        }
    }
}
package com.example.demo_job.dtos;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class JobResponse {
    private List<JobDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
    
    public JobResponse(Page<JobDto> jobPage) {
        this.content = jobPage.getContent();
        this.page = jobPage.getNumber();
        this.size = jobPage.getSize();
        this.totalElements = jobPage.getTotalElements();
        this.totalPages = jobPage.getTotalPages();
        this.last = jobPage.isLast();
    }
}
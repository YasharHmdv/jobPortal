package com.example.demo_job.model;

import lombok.*;

@Data
@RequiredArgsConstructor
public class DjinniJobItem {
    private String title;
    private String company;
    private String location;
    private String jobType;
    private String salary;
    private String description;
    private String postedDate;
    private String jobUrl;
    // other Djinni-specific fields
}
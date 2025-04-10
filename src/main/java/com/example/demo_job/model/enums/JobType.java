package com.example.demo_job.model.enums;

public enum JobType {
    FULL_TIME("Full-time"),
    PART_TIME("Part-time"),
    CONTRACT("Contract"),
    INTERNSHIP("Internship"),
    FREELANCE("Freelance");
    
    private final String displayName;
    
    JobType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
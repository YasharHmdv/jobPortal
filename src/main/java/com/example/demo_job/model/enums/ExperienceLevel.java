package com.example.demo_job.model.enums;

public enum ExperienceLevel {
    ENTRY("Entry"),
    MID("Mid"),
    SENIOR("Senior"),
    LEAD("Lead");
    
    private final String displayName;
    
    ExperienceLevel(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
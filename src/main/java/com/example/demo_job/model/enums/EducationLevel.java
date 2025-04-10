package com.example.demo_job.model.enums;

public enum EducationLevel {
    HIGH_SCHOOL("High School"),
    ASSOCIATE("Associate"),
    BACHELOR("Bachelor's"),
    MASTER("Master's"),
    DOCTORATE("Doctorate"),
    OTHER("Other");
    
    private final String displayName;
    
    EducationLevel(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
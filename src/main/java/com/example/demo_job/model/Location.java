package com.example.demo_job.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Embeddable
@Data
@RequiredArgsConstructor
public class Location {
    private String city;
    private String state;
    private String country;
    
    @Column(name = "is_hybrid")
    private boolean hybrid;
    
}
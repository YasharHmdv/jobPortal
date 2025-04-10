package com.example.demo_job.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Data
@RequiredArgsConstructor
public class SalaryRange {
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String currency;
    
    // Constructors, getters, setters
}
package com.example.demo_job.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LocationDto {
    private String city;
    private String state;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    private boolean hybrid;
}
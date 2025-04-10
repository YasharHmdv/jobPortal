package com.example.demo_job.dtos;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalaryRangeDto {
    @PositiveOrZero(message = "Minimum salary must be positive or zero")
    private BigDecimal minSalary;
    
    @PositiveOrZero(message = "Maximum salary must be positive or zero")
    private BigDecimal maxSalary;
    
    private String currency;
}
package com.example.demo_job.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "djinni")
@Data
public class DjinniConfig {
    private String baseUrl;
    private String dashboardPath;
    
    public String getFullDashboardUrl() {
        return baseUrl + dashboardPath;
    }
}
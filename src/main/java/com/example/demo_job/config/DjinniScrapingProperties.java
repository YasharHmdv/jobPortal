package com.example.demo_job.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "djinni")
public class DjinniScrapingProperties {
    private String baseUrl = "https://djinni.co";
    private long scrapeInterval = 86400000; // 24 hours in milliseconds
    private List<String> targetCountries = List.of("Worldwide", "Azerbaijan");
    private boolean includeRelocationOffers = true;
}
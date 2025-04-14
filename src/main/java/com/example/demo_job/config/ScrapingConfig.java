package com.example.demo_job.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableScheduling
public class ScrapingConfig {

    @Value("${djinni.scrape-interval:86400000}") // Default: 24 hours
    private long scrapeInterval;

    @Bean
    public ScheduledExecutorService scrapingScheduler() {
        return Executors.newScheduledThreadPool(1);
    }

    @Bean
    public DjinniScrapingProperties djinniScrapingProperties() {
        return new DjinniScrapingProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "djinni")
    public DjinniScrapingProperties djinniProperties() {
        return new DjinniScrapingProperties();
    }
}


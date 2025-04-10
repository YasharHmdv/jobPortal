package com.example.demo_job.config;

import com.example.demo_job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig {
    private final JobService jobService;
    @Scheduled(cron = "0 0 3 * * ?") // Runs daily at 3 AM
    public void scheduleJobLoading() {
        jobService.loadJobsFromExternalSource();
    }
}
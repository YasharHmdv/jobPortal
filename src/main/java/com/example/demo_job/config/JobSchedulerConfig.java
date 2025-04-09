package com.example.demo_job.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class JobSchedulerConfig {
    
    private final JobLauncher jobLauncher;
    private final Job scrapeDjinniJobsJob;
    private final JobExplorer jobExplorer;
    
    public JobSchedulerConfig(JobLauncher jobLauncher, 
                            @Qualifier("scrapeDjinniJobsJob") Job job,
                            JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.scrapeDjinniJobsJob = job;
        this.jobExplorer = jobExplorer;
    }
    
    @Scheduled(cron = "0 0 12 * * ?") // Daily at noon
    public void perform() throws Exception {
        JobParameters params = new JobParametersBuilder(jobExplorer)
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        
        jobLauncher.run(scrapeDjinniJobsJob, params);
    }
}
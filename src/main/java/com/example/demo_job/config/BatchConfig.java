package com.example.demo_job.config;

import com.example.demo_job.model.DjinniJobItem;
import com.example.demo_job.model.JobListing;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public Job scrapeDjinniJobsJob(JobRepository jobRepository, Step scrapeStep) {
        return new JobBuilder("scrapeDjinniJobsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(scrapeStep)
                .build();
    }
    @Bean
    public Step scrapeStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           ItemReader<DjinniJobItem> reader,
                           ItemProcessor<DjinniJobItem, JobListing> processor,
                           ItemWriter<JobListing> writer) {
        return new StepBuilder("scrapeStep", jobRepository)
                .<DjinniJobItem, JobListing>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(5);
        return taskExecutor;
    }
}

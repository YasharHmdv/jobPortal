package com.example.demo_job.config;

import com.example.demo_job.dtos.JobDto;
import com.example.demo_job.dtos.JobPostDto;
import com.example.demo_job.dtos.JobSearchCriteria;
import com.example.demo_job.dtos.JobSearchRequest;
import com.example.demo_job.model.Company;
import com.example.demo_job.model.Industry;
import com.example.demo_job.model.Job;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface JobMapper {
    JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);
    Job toEntity(JobPostDto dto);
    JobDto toDto(Job entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "industry", ignore = true)
    void updateEntityFromDto(JobPostDto dto, @MappingTarget Job entity);
    JobSearchCriteria toSearchCriteria(JobSearchRequest request);

    
    default String map(Company company) {
        return company != null ? company.getName() : null;
    }
    
    default String map(Industry industry) {
        return industry != null ? industry.getName() : null;
    }
}
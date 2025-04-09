package com.example.demo_job.repo;

import com.example.demo_job.model.JobListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobListingRepository extends JpaRepository<JobListing,Long> {
    @Query("SELECT j FROM JobListing j WHERE " +
            "(:location IS NULL OR j.location LIKE %:location%) AND " +
            "(:jobType IS NULL OR j.jobType = :jobType) AND " +
            "(:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) AND " +
            "(:industry IS NULL OR j.industry = :industry) AND " +
            "(:keyword IS NULL OR " +
            "j.jobTitle LIKE %:keyword% OR " +
            "j.jobDescription LIKE %:keyword% OR " +
            "j.tags LIKE %:keyword%)")
    Page<JobListing> findByFilters(@Param("location") String location,
                                   @Param("jobType") String jobType,
                                   @Param("experienceLevel") String experienceLevel,
                                   @Param("industry") String industry,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);

    @Query("SELECT j FROM JobListing j WHERE " +
            "j.jobTitle LIKE %:query% OR " +
            "j.jobDescription LIKE %:query% OR " +
            "j.companyName LIKE %:query% OR " +
            "j.tags LIKE %:query%")
    Page<JobListing> search(@Param("query") String query, Pageable pageable);
}

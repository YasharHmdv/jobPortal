package com.example.demo_job.repo;

import com.example.demo_job.model.Company;
import com.example.demo_job.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface CustomJobRepository extends JpaRepository<Job, Long> {
    @Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END FROM Job j WHERE j.title = :title AND j.company.id = :companyId")
    boolean existsByTitleAndCompany(String title, Company company);
}

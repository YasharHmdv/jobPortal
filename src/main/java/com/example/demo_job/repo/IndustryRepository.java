package com.example.demo_job.repo;


import com.example.demo_job.model.Industry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndustryRepository extends JpaRepository<Industry,Long> {
    Optional<Industry> findByName(String name);
}

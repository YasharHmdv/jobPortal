package com.example.demo_job.service;

import com.example.demo_job.dtos.JobSearchCriteria;
import com.example.demo_job.model.Job;
import com.example.demo_job.model.enums.ExperienceLevel;
import com.example.demo_job.model.enums.JobType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Set;

public class JobSpecification {

    public static Specification<Job> withCriteria(JobSearchCriteria criteria) {
        return Specification.where(withLocation(criteria.getLocation()))
                .and(withJobType(criteria.getJobType()))
                .and(withExperienceLevel(criteria.getExperienceLevel()))
                .and(withIndustry(criteria.getIndustry()))
                .and(withTags(criteria.getTags()))
                .and(withRemote(criteria.getRemote()))
                .and(withOffersRelocation(criteria.getOffersRelocation()))
                .and(withPostedAfter(criteria.getPostedAfter()));
    }

    private static Specification<Job> withLocation(String location) {
        return (root, query, cb) -> location == null ? null :
            cb.or(
                cb.like(cb.lower(root.get("location").get("city")), "%" + location.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("location").get("state")), "%" + location.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("location").get("country")), "%" + location.toLowerCase() + "%")
            );
    }

    private static Specification<Job> withJobType(JobType jobType) {
        return (root, query, cb) -> jobType == null ? null :
            cb.equal(root.get("jobType"), jobType);
    }

    private static Specification<Job> withExperienceLevel(ExperienceLevel level) {
        return (root, query, cb) -> level == null ? null :
            cb.equal(root.get("experienceLevel"), level);
    }

    private static Specification<Job> withIndustry(String industry) {
        return (root, query, cb) -> industry == null ? null :
            cb.equal(root.join("industry").get("name"), industry);
    }

    private static Specification<Job> withTags(Set<String> tags) {
        return (root, query, cb) -> tags == null || tags.isEmpty() ? null :
            cb.isTrue(root.join("tags").in(tags));
    }

    private static Specification<Job> withRemote(Boolean remote) {
        return (root, query, cb) -> remote == null ? null :
            cb.equal(root.get("remote"), remote);
    }

    private static Specification<Job> withOffersRelocation(Boolean offersRelocation) {
        return (root, query, cb) -> offersRelocation == null ? null :
            cb.equal(root.get("offersRelocation"), offersRelocation);
    }

    private static Specification<Job> withPostedAfter(LocalDate date) {
        return (root, query, cb) -> date == null ? null :
            cb.greaterThanOrEqualTo(root.get("postedDate"), date);
    }
}
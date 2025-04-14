package com.example.demo_job.service;

import com.example.demo_job.model.Company;
import com.example.demo_job.model.Job;
import com.example.demo_job.model.Location;
import com.example.demo_job.repo.CompanyRepository;
import com.example.demo_job.repo.CustomJobRepository;
import com.example.demo_job.repo.IndustryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DjinniScraperService {

    private final CustomJobRepository customJobRepository;
    private final CompanyRepository companyRepository;
    private final IndustryRepository industryRepository;
    private final CustomJobRepository jobService;
    private final CompanyService companyService;
    private final IndustryService industryService;
    private final RestTemplate restTemplate;

    @Value("${djinni.scraping.base-url}")
    private String baseUrl;

    @Value("${djinni.scraping.jobs-per-page}")
    private int jobsPerPage;

    @Value("${djinni.scraping.max-pages}")
    private int maxPages;

    @Value("${djinni.credentials.email}")
    private String email;

    @Value("${djinni.credentials.password}")
    private String password;

    @Value("${djinni.base-url:https://djinni.co}")
    private String djinniBaseUrl;

    @Async
    public void scrapeAndSaveJobs() {
        try {
            String jobsUrl = djinniBaseUrl + "/jobs/?keywords=Remote";
            ResponseEntity<String> response = restTemplate.getForEntity(jobsUrl, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                List<Job> jobs = parseJobs(response.getBody());
                jobService.saveAllJobs(jobs);
            }
        } catch (Exception e) {
            log.error("Error scraping Djinni jobs", e);
        }
    }

    private List<Job> parseJobs(String html) {
        List<Job> jobs = new ArrayList<>();
        Document doc = Jsoup.parse(html);

        Elements jobElements = doc.select(".list-jobs__item");
        for (Element jobElement : jobElements) {
            try {
                Job job = parseJobElement(jobElement);
                if (shouldIncludeJob(job)) {
                    jobs.add(job);
                }
            } catch (Exception e) {
                log.error("Error parsing job element", e);
            }
        }

        return jobs;
    }

    private Job parseJobElement(Element jobElement) {
        Job job = new Job();

        // Parse title
        String title = jobElement.select(".list-jobs__title a").text();
        job.setTitle(title);

        // Parse company
        String companyName = jobElement.select(".list-jobs__details__info a").first().text();
        Company company = companyService.findOrCreateCompany(companyName);
        job.setCompany(company);

        // Parse location
        Location location = new Location();
        location.setRemote(true); // All Djinni jobs we're scraping are remote
        job.setLocation(location);

        // Parse job type
        String jobTypeText = jobElement.select(".job-list-item__job-type").text();
        job.setJobType(parseJobType(jobTypeText));

        // Parse description and requirements
        String description = jobElement.select(".list-jobs__description").text();
        job.setDescription(description);

        // Parse posted date
        String postedDateText = jobElement.select(".job-list-item__counts .text-date").text();
        job.setPostedDate(parsePostedDate(postedDateText));

        // Parse tags
        Set<String> tags = jobElement.select(".job-list-item__tags span").stream()
                .map(Element::text)
                .collect(Collectors.toSet());
        job.setTags(tags);

        // Parse source URL
        String relativeUrl = jobElement.select(".list-jobs__title a").attr("href");
        job.setSourceUrl(djinniBaseUrl + relativeUrl);

        // Parse countries (from your requirement)
        Set<String> countries = parseCountries(jobElement);
        job.setCountries(countries);

        // Parse relocation info
        job.setOffersRelocation(parseRelocationInfo(jobElement));

        return job;
    }

    private boolean shouldIncludeJob(Job job) {
        // Filter according to requirements: Remote and Worldwide or includes Azerbaijan
        return job.getLocation().isRemote() &&
                (job.getCountries().contains("Worldwide") ||
                        job.getCountries().contains("Azerbaijan") ||
                        job.isOffersRelocation());
    }

    // Helper methods for parsing specific fields...
}

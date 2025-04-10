package com.example.demo_job.service.impl;

import com.example.demo_job.dtos.JobPostDto;
import com.example.demo_job.dtos.LocationDto;
import com.example.demo_job.dtos.SalaryRangeDto;
import com.example.demo_job.model.enums.JobType;
import com.example.demo_job.service.JobScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DjinniJobScraperService implements JobScraperService {

    private final WebClient djinniWebClient;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");

    @Override
    public List<JobPostDto> scrapeDjinniJobs() {
        List<JobPostDto> jobs = new ArrayList<>();
        int page = 1;
        boolean hasMorePages = true;
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);

        while (hasMorePages) {
            String url = String.format("/jobs/?page=%d", page);
            String htmlContent = fetchDjinniPage(url).block();

            Document doc = Jsoup.parse(htmlContent);
            Elements jobElements = doc.select(".list-jobs__item");

            if (jobElements.isEmpty()) {
                hasMorePages = false;
                continue;
            }

            for (Element jobElement : jobElements) {
                try {
                    JobPostDto job = parseJobElement(jobElement);
                    if (job.getPostedDate().isAfter(threeMonthsAgo)) {
                        jobs.add(job);
                    }
                } catch (Exception e) {
                    log.error("Error parsing job element: {}", e.getMessage());
                }
            }

            page++;
        }

        return jobs;
    }

    private Mono<String> fetchDjinniPage(String url) {
        return djinniWebClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("Error fetching Djinni page: {}", e.getMessage()));
    }

    private JobPostDto parseJobElement(Element jobElement) {
        JobPostDto job = new JobPostDto();

        // Parse basic info
        job.setTitle(jobElement.select(".job-list-item__title").text());
        job.setCompanyName(jobElement.select(".job-list-item__company").text());

        // Parse location and remote status
        String locationText = jobElement.select(".location-text").text();
        job.setRemote(locationText.contains("Remote") || locationText.contains("Worldwide"));
        job.setLocation(parseLocation(locationText));

        // Parse countries
        job.setCountries(parseCountries(jobElement));

        // Parse job type
        job.setJobType(parseJobType(jobElement.select(".job-list-item__job-type").text()));

        // Parse posted date
        String dateText = jobElement.select(".job-list-item__counts > span:first-child").text()
                .replace("Posted ", "");
        job.setPostedDate(LocalDate.parse(dateText, dateFormatter));

        // Parse description
        job.setDescription(jobElement.select(".job-list-item__description").text());

        // Parse requirements
        job.setRequirements(jobElement.select(".job-list-item__employment-info").text());

        // Parse salary if available
        String salaryText = jobElement.select(".public-salary-item").text();
        if (!salaryText.isEmpty()) {
            job.setSalaryRange(parseSalary(salaryText));
        }

        // Parse tags
        Set<String> tags = jobElement.select(".job-list-item__tags span").eachText().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        job.setTags(tags);

        // Parse source URL
        String relativeUrl = jobElement.select(".job-list-item__title a").attr("href");
        job.setSourceUrl("https://djinni.co" + relativeUrl);

        // Parse relocation info
        job.setOffersRelocation(jobElement.text().contains("relocation"));

        return job;
    }

    private LocationDto parseLocation(String locationText) {
        LocationDto location = new LocationDto();
        
        if (locationText.contains("Remote")) {
            location.setCountry("Remote");
        } else if (locationText.contains("Worldwide")) {
            location.setCountry("Worldwide");
        } else {
            // Parse city/country from text like "Kyiv, Ukraine" or "New York, USA"
            String[] parts = locationText.split(",");
            if (parts.length >= 1) location.setCity(parts[0].trim());
            if (parts.length >= 2) location.setCountry(parts[1].trim());
        }
        
        location.setHybrid(locationText.contains("Hybrid"));
        return location;
    }

    private Set<String> parseCountries(Element jobElement) {
        // Check for country information in the job details
        String text = jobElement.text();
        Set<String> countries = new HashSet<>();
        
        if (text.contains("Worldwide")) {
            countries.add("Worldwide");
        } else {
            // Look for patterns like "Countries: Ukraine, Poland, Germany"
            Pattern pattern = Pattern.compile("Countries?: ([\\w,\\s]+)");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String[] countryArray = matcher.group(1).split(",");
                for (String country : countryArray) {
                    countries.add(country.trim());
                }
            }
        }
        
        return countries;
    }

    private JobType parseJobType(String jobTypeText) {
        if (jobTypeText.contains("Full-time")) return JobType.FULL_TIME;
        if (jobTypeText.contains("Part-time")) return JobType.PART_TIME;
        if (jobTypeText.contains("Contract")) return JobType.CONTRACT;
        if (jobTypeText.contains("Internship")) return JobType.INTERNSHIP;
        if (jobTypeText.contains("Freelance")) return JobType.FREELANCE;
        return JobType.FULL_TIME; // default
    }

    private SalaryRangeDto parseSalary(String salaryText) {
        SalaryRangeDto salaryRange = new SalaryRangeDto();
        salaryRange.setCurrency("$"); // Default, can be parsed from text
        
        // Handle different salary formats:
        // "$2000-4000" or "From $3000" or "Up to $5000"
        Pattern rangePattern = Pattern.compile("\\$(\\d+)\\s*-\\s*\\$(\\d+)");
        Pattern fromPattern = Pattern.compile("From\\s*\\$(\\d+)");
        Pattern toPattern = Pattern.compile("Up to\\s*\\$(\\d+)");
        Pattern exactPattern = Pattern.compile("\\$(\\d+)");
        
        Matcher matcher = rangePattern.matcher(salaryText);
        if (matcher.find()) {
            salaryRange.setMinSalary(new BigDecimal(matcher.group(1)));
            salaryRange.setMaxSalary(new BigDecimal(matcher.group(2)));
        } else {
            matcher = fromPattern.matcher(salaryText);
            if (matcher.find()) {
                salaryRange.setMinSalary(new BigDecimal(matcher.group(1)));
            }
            
            matcher = toPattern.matcher(salaryText);
            if (matcher.find()) {
                salaryRange.setMaxSalary(new BigDecimal(matcher.group(1)));
            }
            
            matcher = exactPattern.matcher(salaryText);
            if (matcher.find() && salaryRange.getMinSalary() == null) {
                salaryRange.setMinSalary(new BigDecimal(matcher.group(1)));
                salaryRange.setMaxSalary(new BigDecimal(matcher.group(1)));
            }
        }
        
        return salaryRange;
    }
}
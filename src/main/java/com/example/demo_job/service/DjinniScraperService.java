package com.example.demo_job.service;

import com.example.demo_job.model.Company;
import com.example.demo_job.model.Job;
import com.example.demo_job.model.Location;
import com.example.demo_job.model.SalaryRange;
import com.example.demo_job.repo.CompanyRepository;
import com.example.demo_job.repo.CustomJobRepository;
import com.example.demo_job.repo.IndustryRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DjinniScraperService {
    
    private final CustomJobRepository customJobRepository;
    private final CompanyRepository companyRepository;
    private final IndustryRepository industryRepository;
    
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

    public int scrapeAndSaveJobs() throws Exception {
        // Initialize web driver (you'll need to add Selenium dependency)
        WebDriverManager.chromedriver().driverVersion("135.0.7049.85").setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new", // New headless mode
                "--disable-gpu",
                "--window-size=1920,1080",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-blink-features=AutomationControlled"
        );

        // Disable CDP if not needed
        options.setExperimentalOption("excludeSwitches",
                Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);


        try {
            // Login to Djinni
            login(driver);

            // Scrape jobs
            List<Job> jobs = scrapeJobs(driver);

            // Save jobs
            return saveJobs(jobs);

        } finally {
            driver.quit();
        }
    }

    private void login(WebDriver driver) {
        driver.get(baseUrl + "/login");

        WebElement emailField = driver.findElement(By.name("email"));
        emailField.sendKeys(email);

        WebElement passwordField = driver.findElement(By.name("password"));
        passwordField.sendKeys(password);

        passwordField.sendKeys(Keys.RETURN);
    }

    private List<Job> scrapeJobs(WebDriver driver) {
        List<Job> jobs = new ArrayList<>();

        try {
            for (int page = 1; page <= maxPages; page++) {
                String url = baseUrl + "/jobs/?page=" + page;
                log.info("Scraping page {}: {}", page, url);

                driver.get(url);
                waitForPageLoad(driver);

                List<WebElement> jobElements = driver.findElements(
                        By.cssSelector(".list-jobs__item:not(.sponsored-job)"));

                if (jobElements.isEmpty()) {
                    log.warn("No jobs found on page {}", page);
                    break;
                }

                for (WebElement jobElement : jobElements) {
                    try {
                        Job job = parseJobElement(driver, jobElement);
                        if (job != null) {
                            jobs.add(job);
                        }
                    } catch (Exception e) {
                        log.error("Error parsing job element", e);
                    }
                }

                if (shouldStopPagination(driver)) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Error during scraping", e);
            throw e;
        }

        return jobs;
    }

    private void waitForPageLoad(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> ((JavascriptExecutor) d)
                        .executeScript("return document.readyState")
                        .equals("complete"));
    }

    private boolean shouldStopPagination(WebDriver driver) {
        try {
            return driver.findElements(By.cssSelector(".pagination li.active + li.disabled"))
                    .stream()
                    .anyMatch(el -> el.getText().contains("Next"));
        } catch (Exception e) {
            return false;
        }
    }

    private Job parseJobElement(WebDriver driver, WebElement jobElement) {
        try {
            // Scroll element into view
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(true);", jobElement);

            Job job = new Job();
            // ... [your parsing logic]

            return job;
        } catch (StaleElementReferenceException e) {
            log.warn("Stale element reference, retrying...");
            return null;
        }
    }

    private Job parseJobElement(WebElement jobElement) {
        Job job = new Job();

        // Extract basic info
        String title = jobElement.findElement(By.cssSelector(".job-list-item__title")).getText();
        job.setTitle(title);

        // Extract company
        String companyName = jobElement.findElement(By.cssSelector(".job-list-item__company")).getText();
        Company company = companyRepository.findByName(companyName)
                .orElseGet(() -> companyRepository.save(new Company(companyName)));
        job.setCompany(company);

        // Extract location info
        String locationText = jobElement.findElement(By.cssSelector(".location-text")).getText();
        Location location = parseLocation(locationText);
        job.setLocation(location);

        // Extract job type (remote/office)
        boolean isRemote = locationText.toLowerCase().contains("remote");
        job.setRemote(isRemote);

        // Extract posted date
        String postedDateText = jobElement.findElement(By.cssSelector(".job-list-item__date")).getText();
        LocalDate postedDate = parsePostedDate(postedDateText);
        job.setPostedDate(postedDate);

        // Extract description (simplified)
        String description = jobElement.findElement(By.cssSelector(".job-list-item__description")).getText();
        job.setDescription(description);

        // Extract salary if available
        try {
            String salaryText = jobElement.findElement(By.cssSelector(".public-salary-item")).getText();
            SalaryRange salaryRange = parseSalary(salaryText);
            job.setSalaryRange(salaryRange);
        } catch (NoSuchElementException e) {
            // Salary not available for this job
        }

        // Extract source URL
        String relativeUrl = jobElement.findElement(By.cssSelector(".job-list-item__link")).getAttribute("href");
        job.setSourceUrl(baseUrl + relativeUrl);

        return job;
    }

    private Location parseLocation(String locationText) {
        Location location = new Location();

        if (locationText.toLowerCase().contains("remote")) {
            location.setCountry("Remote");

        } else {
            // Simple parsing - you might need more sophisticated logic
            String[] parts = locationText.split(",");
            if (parts.length > 0) location.setCity(parts[0].trim());
            if (parts.length > 1) location.setCountry(parts[1].trim());
        }

        return location;
    }

    private LocalDate parsePostedDate(String postedDateText) {
        if (postedDateText.contains("today")) {
            return LocalDate.now();
        } else if (postedDateText.contains("yesterday")) {
            return LocalDate.now().minusDays(1);
        } else {
            // Parse "X days ago"
            int daysAgo = Integer.parseInt(postedDateText.replaceAll("\\D+", ""));
            return LocalDate.now().minusDays(daysAgo);
        }
    }

    private SalaryRange parseSalary(String salaryText) {
        SalaryRange salaryRange = new SalaryRange();

        // Simple parsing - adjust as needed
        String cleanText = salaryText.replaceAll("[^\\d\\s]", "").trim();
        String[] parts = cleanText.split("\\s+");

        if (parts.length >= 2) {
            salaryRange.setMinSalary(new BigDecimal(parts[0]));
            salaryRange.setMaxSalary(new BigDecimal(parts[1]));
            salaryRange.setCurrency("USD"); // Adjust based on actual data
        }

        return salaryRange;
    }

    private int saveJobs(List<Job> jobs) {
        int savedCount = 0;

        for (Job job : jobs) {
            try {
                // Check if job already exists by title and company
                if (!customJobRepository.existsByTitleAndCompany(job.getTitle(), job.getCompany())) {
                    customJobRepository.save(job);
                    savedCount++;
                }
            } catch (Exception e) {
                // Log error but continue with other jobs
                System.err.println("Error saving job: " + e.getMessage());
            }
        }

        return savedCount;
    }
}
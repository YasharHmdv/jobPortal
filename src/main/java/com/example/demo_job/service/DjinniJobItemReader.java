package com.example.demo_job.service;

import com.example.demo_job.config.DjinniConfig;
import com.example.demo_job.model.DjinniJobItem;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class DjinniJobItemReader implements ItemReader<DjinniJobItem> {
    private static final String DJINNI_URL = "https://djinni.co";
    private static final String DASHBOARD_PATH = "/my/dashboard/";
    
    private final DjinniAuthService authService;
    private final DjinniConfig djinniConfig;
    private Iterator<DjinniJobItem> jobItemsIterator;

    
    @Override
    public DjinniJobItem read() throws Exception {
        if (jobItemsIterator == null || !jobItemsIterator.hasNext()) {
            List<DjinniJobItem> jobs = scrapeDjinniJobs();
            if (jobs.isEmpty()) {
                return null;
            }
            jobItemsIterator = jobs.iterator();
        }
        return jobItemsIterator.next();
    }
    
    // ... rest of the implementation remains the same
    private List<DjinniJobItem> scrapeDjinniJobs() throws IOException {
        List<DjinniJobItem> jobs = new ArrayList<>();
        String url = djinniConfig.getFullDashboardUrl();
        Document doc = Jsoup.connect(url).get();

        try {
            // You'll need to handle authentication here
            Connection.Response loginForm = Jsoup.connect(DJINNI_URL)
                    .method(Connection.Method.GET)
                    .execute();

            // Perform login (you'll need actual credentials)
            Document dashboard = Jsoup.connect(DJINNI_URL)
                    .data("email", "your@email.com")
                    .data("password", "yourpassword")
                    .cookies(loginForm.cookies())
                    .post();

            // Scrape job listings from the last 3 months
            Elements jobElements = dashboard.select(".job-list-item");
            for (Element jobElement : jobElements) {
                DjinniJobItem jobItem = new DjinniJobItem();
                // Extract data from HTML elements
                jobItem.setTitle(jobElement.select(".job-title").text());
                jobItem.setCompany(jobElement.select(".company-name").text());
                // ... extract other fields

                // Check if job is Remote/Worldwide or includes Azerbaijan
                String location = jobElement.select(".location").text();
                if (location.contains("Remote") ||
                        location.contains("Worldwide") ||
                        location.contains("Azerbaijan")) {
                    jobs.add(jobItem);
                }
            }
        } catch (IOException e) {
            // Handle errors appropriately
            e.printStackTrace();
        }

        return jobs;
    }
}
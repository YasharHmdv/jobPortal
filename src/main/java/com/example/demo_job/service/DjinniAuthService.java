package com.example.demo_job.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class DjinniAuthService {
    
    @Value("${djinni.credentials.email}")
    private String email;
    
    @Value("${djinni.credentials.password}")
    private String password;
    
    private Map<String, String> cookies;
    
    public Map<String, String> authenticate() throws IOException {
        Connection.Response loginForm = Jsoup.connect("https://djinni.co/login")
                .method(Connection.Method.GET)
                .execute();
        
        Document authPage = Jsoup.connect("https://djinni.co/login")
                .data("email", email)
                .data("password", password)
                .cookies(loginForm.cookies())
                .post();
        
        this.cookies = loginForm.cookies();
        return cookies;
    }
    
    public Map<String, String> getCookies() {
        return cookies;
    }
}
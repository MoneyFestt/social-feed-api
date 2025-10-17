package com.example.socialfeedapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class SocialFeedApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SocialFeedApiApplication.class, args);
    }
}
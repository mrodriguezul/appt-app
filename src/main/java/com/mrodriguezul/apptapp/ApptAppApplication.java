package com.mrodriguezul.apptapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import com.mrodriguezul.apptapp.config.JwtProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(JwtProperties.class)
public class ApptAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApptAppApplication.class, args);
    }

}

// Note: This is a basic Spring Boot application starter class.
// It serves as the entry point for the Spring Boot application.
// The @SpringBootApplication annotation enables auto-configuration, component scanning, and configuration properties.

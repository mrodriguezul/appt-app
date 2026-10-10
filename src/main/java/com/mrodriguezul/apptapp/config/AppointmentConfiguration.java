package com.mrodriguezul.apptapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AppointmentConfiguration {
    @Bean
    public Clock appointmentClock() {
        return Clock.systemDefaultZone();
    }
}

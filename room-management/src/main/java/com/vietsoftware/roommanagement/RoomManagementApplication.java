package com.vietsoftware.roommanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application entry point for the Room Management Microservice.
 *
 * @author VietSoftware
 * @version 1.0.0
 */
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class RoomManagementApplication {

    /**
     * Starts the Spring Boot Room Management Application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(RoomManagementApplication.class, args);
    }

}

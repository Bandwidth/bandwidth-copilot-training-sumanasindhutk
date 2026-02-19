package com.journal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Daily Journal webapp.
 * A Spring Boot application for managing daily journal entries with local file storage.
 * 
 * Features:
 * - Water intake tracking
 * - Exercise logging
 * - Mood tracking
 * - To-do list with 1-5 star priorities
 * - Rich text notes
 * - Search and filtering
 * - Date-based navigation
 * 
 * @author GitHub Copilot
 * @version 1.0.0
 */
@SpringBootApplication
public class JournalApplication {

    /**
     * Application entry point.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(JournalApplication.class, args);
    }
}

package com.journal.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.journal.exception.JournalStorageException;
import com.journal.model.JournalEntry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * Repository for managing journal entries in the file system.
 * Each entry is stored as a separate JSON file.
 * File naming pattern: {date}_{id}.json (e.g., 2026-02-19_abc123.json)
 */
@Repository
@Slf4j
public class JournalFileRepository {

    private final ObjectMapper objectMapper;
    private final Path storagePath;

    /**
     * Constructor with dependency injection.
     *
     * @param storagePath The base directory for storing journal files
     */
    public JournalFileRepository(@Value("${journal.storage.path}") String storagePath) {
        this.storagePath = Paths.get(storagePath);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Initializes the storage directory on application startup.
     * Creates the directory if it doesn't exist.
     */
    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
                log.info("Created journal storage directory: {}", storagePath);
            } else {
                log.info("Using existing journal storage directory: {}", storagePath);
            }
        } catch (IOException e) {
            throw new JournalStorageException("Failed to initialize storage directory: " + storagePath, e);
        }
    }

    /**
     * Saves a journal entry to a JSON file.
     * Creates a new file or overwrites existing one.
     *
     * @param entry The journal entry to save
     * @throws JournalStorageException if the save operation fails
     */
    public void save(JournalEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Journal entry cannot be null");
        }
        if (entry.getId() == null || entry.getDate() == null) {
            throw new IllegalArgumentException("Journal entry must have an ID and date");
        }

        Path filePath = getFilePath(entry.getDate(), entry.getId());
        
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entry);
            Files.writeString(filePath, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.debug("Saved journal entry: {}", filePath);
        } catch (IOException e) {
            throw new JournalStorageException("Failed to save journal entry: " + entry.getId(), e);
        }
    }

    /**
     * Finds a journal entry by its ID.
     *
     * @param id The entry ID
     * @return Optional containing the entry if found, empty otherwise
     * @throws JournalStorageException if the read operation fails
     */
    public Optional<JournalEntry> findById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Entry ID cannot be null or blank");
        }

        try (Stream<Path> paths = Files.list(storagePath)) {
            return paths
                    .filter(path -> path.getFileName().toString().endsWith("_" + id + ".json"))
                    .findFirst()
                    .map(this::readEntry);
        } catch (IOException e) {
            throw new JournalStorageException("Failed to search for entry: " + id, e);
        }
    }

    /**
     * Finds all journal entries.
     *
     * @return List of all journal entries, sorted by date (newest first)
     * @throws JournalStorageException if the read operation fails
     */
    public List<JournalEntry> findAll() {
        try (Stream<Path> paths = Files.list(storagePath)) {
            return paths
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(this::readEntry)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(JournalEntry::getDate).reversed())
                    .toList();
        } catch (IOException e) {
            throw new JournalStorageException("Failed to list journal entries", e);
        }
    }

    /**
     * Finds journal entries for a specific date.
     *
     * @param date The date to search for
     * @return List of entries for the given date
     * @throws JournalStorageException if the read operation fails
     */
    public List<JournalEntry> findByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        String datePrefix = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        
        try (Stream<Path> paths = Files.list(storagePath)) {
            return paths
                    .filter(path -> path.getFileName().toString().startsWith(datePrefix))
                    .map(this::readEntry)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            throw new JournalStorageException("Failed to find entries for date: " + date, e);
        }
    }

    /**
     * Finds journal entries within a date range (inclusive).
     *
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of entries within the date range, sorted by date (newest first)
     * @throws JournalStorageException if the read operation fails
     */
    public List<JournalEntry> findByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        try (Stream<Path> paths = Files.list(storagePath)) {
            return paths
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(this::readEntry)
                    .filter(Objects::nonNull)
                    .filter(entry -> !entry.getDate().isBefore(startDate) && !entry.getDate().isAfter(endDate))
                    .sorted(Comparator.comparing(JournalEntry::getDate).reversed())
                    .toList();
        } catch (IOException e) {
            throw new JournalStorageException("Failed to find entries for date range", e);
        }
    }

    /**
     * Deletes a journal entry by ID.
     *
     * @param id The entry ID
     * @return true if the entry was deleted, false if not found
     * @throws JournalStorageException if the delete operation fails
     */
    public boolean deleteById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Entry ID cannot be null or blank");
        }

        try (Stream<Path> paths = Files.list(storagePath)) {
            Optional<Path> filePath = paths
                    .filter(path -> path.getFileName().toString().endsWith("_" + id + ".json"))
                    .findFirst();

            if (filePath.isPresent()) {
                Files.delete(filePath.get());
                log.debug("Deleted journal entry: {}", filePath.get());
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new JournalStorageException("Failed to delete entry: " + id, e);
        }
    }

    /**
     * Checks if an entry exists by ID.
     *
     * @param id The entry ID
     * @return true if the entry exists, false otherwise
     */
    public boolean existsById(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }

        try (Stream<Path> paths = Files.list(storagePath)) {
            return paths.anyMatch(path -> path.getFileName().toString().endsWith("_" + id + ".json"));
        } catch (IOException e) {
            throw new JournalStorageException("Failed to check existence of entry: " + id, e);
        }
    }

    /**
     * Gets the file path for a journal entry.
     *
     * @param date The entry date
     * @param id The entry ID
     * @return The full file path
     */
    private Path getFilePath(LocalDate date, String id) {
        String fileName = String.format("%s_%s.json", 
                date.format(DateTimeFormatter.ISO_LOCAL_DATE), 
                id);
        return storagePath.resolve(fileName);
    }

    /**
     * Reads a journal entry from a file.
     *
     * @param path The file path
     * @return The journal entry, or null if reading fails
     */
    private JournalEntry readEntry(Path path) {
        try {
            String json = Files.readString(path);
            return objectMapper.readValue(json, JournalEntry.class);
        } catch (IOException e) {
            log.error("Failed to read journal entry from file: {}", path, e);
            return null;
        }
    }
}

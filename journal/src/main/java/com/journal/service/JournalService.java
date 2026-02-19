package com.journal.service;

import com.journal.model.JournalEntry;
import com.journal.repository.JournalFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for managing journal entries.
 * Provides business logic and validation for journal operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JournalService {

    private final JournalFileRepository repository;

    /**
     * Creates a new journal entry.
     * Generates a unique ID and sets the current timestamp.
     *
     * @param entry The journal entry to create (ID will be generated)
     * @return The created journal entry with generated ID
     */
    public JournalEntry createEntry(JournalEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Journal entry cannot be null");
        }
        if (entry.getDate() == null) {
            throw new IllegalArgumentException("Journal entry must have a date");
        }

        // Validate water intake and exercise minutes
        validateEntry(entry);

        // Generate ID if not present
        if (entry.getId() == null || entry.getId().isBlank()) {
            entry.setId(UUID.randomUUID().toString());
        }

        // Set timestamp
        entry.setTimestamp(LocalDateTime.now());

        repository.save(entry);
        log.info("Created journal entry: {} for date: {}", entry.getId(), entry.getDate());
        
        return entry;
    }

    /**
     * Updates an existing journal entry.
     * Updates the timestamp to the current time.
     *
     * @param id The entry ID
     * @param updatedEntry The updated entry data
     * @return The updated journal entry
     * @throws NoSuchElementException if the entry is not found
     */
    public JournalEntry updateEntry(String id, JournalEntry updatedEntry) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Entry ID cannot be null or blank");
        }
        if (updatedEntry == null) {
            throw new IllegalArgumentException("Updated entry cannot be null");
        }

        // Verify entry exists
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Journal entry not found: " + id);
        }

        // Validate the updated entry
        validateEntry(updatedEntry);

        // Ensure ID matches
        updatedEntry.setId(id);
        
        // Update timestamp
        updatedEntry.setTimestamp(LocalDateTime.now());

        repository.save(updatedEntry);
        log.info("Updated journal entry: {}", id);
        
        return updatedEntry;
    }

    /**
     * Deletes a journal entry by ID.
     *
     * @param id The entry ID
     * @return true if deleted, false if not found
     */
    public boolean deleteEntry(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Entry ID cannot be null or blank");
        }

        boolean deleted = repository.deleteById(id);
        if (deleted) {
            log.info("Deleted journal entry: {}", id);
        } else {
            log.warn("Attempted to delete non-existent entry: {}", id);
        }
        
        return deleted;
    }

    /**
     * Gets a journal entry by ID.
     *
     * @param id The entry ID
     * @return Optional containing the entry if found
     */
    public Optional<JournalEntry> getEntryById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Entry ID cannot be null or blank");
        }

        log.debug("Retrieving journal entry: {}", id);
        return repository.findById(id);
    }

    /**
     * Gets all journal entries.
     *
     * @return List of all entries, sorted by date (newest first)
     */
    public List<JournalEntry> getAllEntries() {
        log.debug("Retrieving all journal entries");
        return repository.findAll();
    }

    /**
     * Gets journal entries for a specific date.
     *
     * @param date The date to search for
     * @return List of entries for the given date
     */
    public List<JournalEntry> getEntriesForDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        log.debug("Retrieving entries for date: {}", date);
        return repository.findByDate(date);
    }

    /**
     * Gets journal entries within a date range.
     *
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of entries within the range, sorted by date (newest first)
     */
    public List<JournalEntry> getEntriesForDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        log.debug("Retrieving entries from {} to {}", startDate, endDate);
        return repository.findByDateRange(startDate, endDate);
    }

    /**
     * Searches journal entries by query string.
     * Searches in notes and to-do descriptions (case-insensitive).
     *
     * @param query The search query
     * @return List of matching entries
     */
    public List<JournalEntry> searchEntries(String query) {
        if (query == null || query.isBlank()) {
            return getAllEntries();
        }

        String lowerQuery = query.toLowerCase();
        log.debug("Searching entries with query: {}", query);

        return repository.findAll().stream()
                .filter(entry -> matchesQuery(entry, lowerQuery))
                .collect(Collectors.toList());
    }

    /**
     * Validates a journal entry.
     *
     * @param entry The entry to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateEntry(JournalEntry entry) {
        if (entry.getWaterIntake() < 0) {
            throw new IllegalArgumentException("Water intake cannot be negative");
        }
        if (entry.getExerciseMinutes() < 0) {
            throw new IllegalArgumentException("Exercise minutes cannot be negative");
        }

        // Validate to-do priorities
        if (entry.getTodos() != null) {
            entry.getTodos().forEach(todo -> {
                if (todo.getPriority() < 1 || todo.getPriority() > 5) {
                    throw new IllegalArgumentException("To-do priority must be between 1 and 5");
                }
            });
        }
    }

    /**
     * Checks if an entry matches a search query.
     *
     * @param entry The entry to check
     * @param lowerQuery The lowercase search query
     * @return true if the entry matches, false otherwise
     */
    private boolean matchesQuery(JournalEntry entry, String lowerQuery) {
        // Search in notes
        if (entry.getNotes() != null && entry.getNotes().toLowerCase().contains(lowerQuery)) {
            return true;
        }

        // Search in mood
        if (entry.getMood() != null && entry.getMood().toLowerCase().contains(lowerQuery)) {
            return true;
        }

        // Search in to-do descriptions
        if (entry.getTodos() != null) {
            return entry.getTodos().stream()
                    .anyMatch(todo -> todo.getDescription() != null && 
                            todo.getDescription().toLowerCase().contains(lowerQuery));
        }

        return false;
    }
}

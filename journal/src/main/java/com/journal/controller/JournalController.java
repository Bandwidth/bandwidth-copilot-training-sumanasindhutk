package com.journal.controller;

import com.journal.model.JournalEntry;
import com.journal.service.JournalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * REST API controller for journal operations.
 * Provides endpoints for CRUD operations, search, and filtering.
 */
@RestController
@RequestMapping("/api/journal")
@RequiredArgsConstructor
@Validated
@Slf4j
public class JournalController {

    private final JournalService journalService;

    /**
     * Gets all journal entries.
     *
     * @return List of all journal entries
     */
    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllEntries() {
        log.debug("GET /api/journal - Retrieving all entries");
        List<JournalEntry> entries = journalService.getAllEntries();
        return ResponseEntity.ok(entries);
    }

    /**
     * Gets a specific journal entry by ID.
     *
     * @param id The entry ID
     * @return The journal entry
     */
    @GetMapping("/{id}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable @NotBlank String id) {
        log.debug("GET /api/journal/{} - Retrieving entry", id);
        return journalService.getEntryById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NoSuchElementException("Journal entry not found: " + id));
    }

    /**
     * Gets journal entries for a specific date.
     *
     * @param date The date (format: yyyy-MM-dd)
     * @return List of entries for the date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<JournalEntry>> getEntriesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.debug("GET /api/journal/date/{} - Retrieving entries for date", date);
        List<JournalEntry> entries = journalService.getEntriesForDate(date);
        return ResponseEntity.ok(entries);
    }

    /**
     * Gets journal entries within a date range.
     *
     * @param start Start date (format: yyyy-MM-dd)
     * @param end End date (format: yyyy-MM-dd)
     * @return List of entries within the range
     */
    @GetMapping("/range")
    public ResponseEntity<List<JournalEntry>> getEntriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        log.debug("GET /api/journal/range?start={}&end={} - Retrieving entries for range", start, end);
        List<JournalEntry> entries = journalService.getEntriesForDateRange(start, end);
        return ResponseEntity.ok(entries);
    }

    /**
     * Searches journal entries by query string.
     *
     * @param q Search query
     * @return List of matching entries
     */
    @GetMapping("/search")
    public ResponseEntity<List<JournalEntry>> searchEntries(
            @RequestParam(required = false, defaultValue = "") String q) {
        log.debug("GET /api/journal/search?q={} - Searching entries", q);
        List<JournalEntry> entries = journalService.searchEntries(q);
        return ResponseEntity.ok(entries);
    }

    /**
     * Creates a new journal entry.
     *
     * @param entry The journal entry to create
     * @return The created entry
     */
    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@Valid @RequestBody JournalEntry entry) {
        log.debug("POST /api/journal - Creating new entry");
        JournalEntry created = journalService.createEntry(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing journal entry.
     *
     * @param id The entry ID
     * @param entry The updated entry data
     * @return The updated entry
     */
    @PutMapping("/{id}")
    public ResponseEntity<JournalEntry> updateEntry(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody JournalEntry entry) {
        log.debug("PUT /api/journal/{} - Updating entry", id);
        JournalEntry updated = journalService.updateEntry(id, entry);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a journal entry.
     *
     * @param id The entry ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable @NotBlank String id) {
        log.debug("DELETE /api/journal/{} - Deleting entry", id);
        boolean deleted = journalService.deleteEntry(id);
        
        if (!deleted) {
            throw new NoSuchElementException("Journal entry not found: " + id);
        }
        
        return ResponseEntity.noContent().build();
    }
}

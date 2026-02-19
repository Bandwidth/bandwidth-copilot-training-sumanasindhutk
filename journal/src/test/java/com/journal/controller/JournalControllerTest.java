package com.journal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.journal.model.JournalEntry;
import com.journal.service.JournalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for JournalController.
 * Tests REST API endpoints.
 */
@WebMvcTest(JournalController.class)
class JournalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JournalService journalService;

    private ObjectMapper objectMapper;
    private JournalEntry testEntry;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        testDate = LocalDate.of(2026, 2, 19);
        testEntry = JournalEntry.builder()
                .id("test-id-123")
                .date(testDate)
                .waterIntake(8)
                .exerciseMinutes(30)
                .mood("Happy")
                .todos(new ArrayList<>())
                .notes("Test notes")
                .build();
    }

    @Test
    void testGetAllEntries_WhenEntriesExist_ThenReturns200() throws Exception {
        // Arrange
        when(journalService.getAllEntries()).thenReturn(List.of(testEntry));

        // Act & Assert
        mockMvc.perform(get("/api/journal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("test-id-123"))
                .andExpect(jsonPath("$[0].mood").value("Happy"));
    }

    @Test
    void testGetEntryById_WhenEntryExists_ThenReturns200() throws Exception {
        // Arrange
        when(journalService.getEntryById("test-id-123")).thenReturn(Optional.of(testEntry));

        // Act & Assert
        mockMvc.perform(get("/api/journal/test-id-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id-123"))
                .andExpect(jsonPath("$.mood").value("Happy"));
    }

    @Test
    void testGetEntryById_WhenEntryNotFound_ThenReturns404() throws Exception {
        // Arrange
        when(journalService.getEntryById("non-existent"))
                .thenThrow(new NoSuchElementException("Journal entry not found: non-existent"));

        // Act & Assert
        mockMvc.perform(get("/api/journal/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetEntriesByDate_WhenEntriesExist_ThenReturns200() throws Exception {
        // Arrange
        when(journalService.getEntriesForDate(testDate)).thenReturn(List.of(testEntry));

        // Act & Assert
        mockMvc.perform(get("/api/journal/date/2026-02-19"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2026-02-19"));
    }

    @Test
    void testGetEntriesByDateRange_WhenValidRange_ThenReturns200() throws Exception {
        // Arrange
        LocalDate start = LocalDate.of(2026, 2, 1);
        LocalDate end = LocalDate.of(2026, 2, 28);
        when(journalService.getEntriesForDateRange(start, end)).thenReturn(List.of(testEntry));

        // Act & Assert
        mockMvc.perform(get("/api/journal/range")
                        .param("start", "2026-02-01")
                        .param("end", "2026-02-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("test-id-123"));
    }

    @Test
    void testSearchEntries_WhenQueryProvided_ThenReturns200() throws Exception {
        // Arrange
        when(journalService.searchEntries("Happy")).thenReturn(List.of(testEntry));

        // Act & Assert
        mockMvc.perform(get("/api/journal/search")
                        .param("q", "Happy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mood").value("Happy"));
    }

    @Test
    void testCreateEntry_WhenValidEntry_ThenReturns201() throws Exception {
        // Arrange
        JournalEntry newEntry = JournalEntry.builder()
                .date(testDate)
                .waterIntake(5)
                .exerciseMinutes(20)
                .mood("Neutral")
                .todos(new ArrayList<>())
                .notes("New entry")
                .build();

        when(journalService.createEntry(any(JournalEntry.class))).thenReturn(testEntry);

        // Act & Assert
        mockMvc.perform(post("/api/journal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEntry)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("test-id-123"));
    }

    @Test
    void testCreateEntry_WhenInvalidEntry_ThenReturns400() throws Exception {
        // Arrange - Entry without required date
        JournalEntry invalidEntry = JournalEntry.builder()
                .waterIntake(5)
                .exerciseMinutes(20)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/journal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEntry)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateEntry_WhenValidEntry_ThenReturns200() throws Exception {
        // Arrange
        JournalEntry updatedEntry = JournalEntry.builder()
                .id("test-id-123")
                .date(testDate)
                .waterIntake(10)
                .exerciseMinutes(45)
                .mood("Energetic")
                .todos(new ArrayList<>())
                .notes("Updated notes")
                .build();

        when(journalService.updateEntry(eq("test-id-123"), any(JournalEntry.class)))
                .thenReturn(updatedEntry);

        // Act & Assert
        mockMvc.perform(put("/api/journal/test-id-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEntry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mood").value("Energetic"))
                .andExpect(jsonPath("$.waterIntake").value(10));
    }

    @Test
    void testUpdateEntry_WhenEntryNotFound_ThenReturns404() throws Exception {
        // Arrange
        when(journalService.updateEntry(eq("non-existent"), any(JournalEntry.class)))
                .thenThrow(new NoSuchElementException("Journal entry not found"));

        JournalEntry entry = JournalEntry.builder()
                .date(testDate)
                .waterIntake(5)
                .exerciseMinutes(20)
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/journal/non-existent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteEntry_WhenEntryExists_ThenReturns204() throws Exception {
        // Arrange
        when(journalService.deleteEntry("test-id-123")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/journal/test-id-123"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteEntry_WhenEntryNotFound_ThenReturns404() throws Exception {
        // Arrange
        when(journalService.deleteEntry("non-existent")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/journal/non-existent"))
                .andExpect(status().isNotFound());
    }
}

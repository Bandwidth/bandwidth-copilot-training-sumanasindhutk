package com.journal.service;

import com.journal.model.JournalEntry;
import com.journal.model.TodoItem;
import com.journal.repository.JournalFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JournalService.
 * Tests business logic and validation.
 */
@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @Mock
    private JournalFileRepository repository;

    @InjectMocks
    private JournalService service;

    private JournalEntry testEntry;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
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
    void testCreateEntry_WhenValidEntry_ThenSavesSuccessfully() {
        // Arrange
        JournalEntry newEntry = JournalEntry.builder()
                .date(testDate)
                .waterIntake(5)
                .exerciseMinutes(20)
                .mood("Neutral")
                .todos(new ArrayList<>())
                .notes("New entry")
                .build();

        // Act
        JournalEntry result = service.createEntry(newEntry);

        // Assert
        assertNotNull(result.getId());
        assertNotNull(result.getTimestamp());
        verify(repository, times(1)).save(any(JournalEntry.class));
    }

    @Test
    void testCreateEntry_WhenNullEntry_ThenThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.createEntry(null));
    }

    @Test
    void testCreateEntry_WhenNullDate_ThenThrowsException() {
        // Arrange
        JournalEntry entryWithoutDate = JournalEntry.builder()
                .waterIntake(5)
                .exerciseMinutes(20)
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.createEntry(entryWithoutDate));
    }

    @Test
    void testCreateEntry_WhenNegativeWaterIntake_ThenThrowsException() {
        // Arrange
        JournalEntry invalidEntry = JournalEntry.builder()
                .date(testDate)
                .waterIntake(-1)
                .exerciseMinutes(20)
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.createEntry(invalidEntry));
    }

    @Test
    void testCreateEntry_WhenInvalidTodoPriority_ThenThrowsException() {
        // Arrange
        TodoItem invalidTodo = TodoItem.builder()
                .id("todo-1")
                .description("Test")
                .priority(6)
                .build();
        
        JournalEntry entryWithInvalidTodo = JournalEntry.builder()
                .date(testDate)
                .waterIntake(5)
                .exerciseMinutes(20)
                .todos(List.of(invalidTodo))
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.createEntry(entryWithInvalidTodo));
    }

    @Test
    void testUpdateEntry_WhenValidEntry_ThenUpdatesSuccessfully() {
        // Arrange
        when(repository.existsById("test-id-123")).thenReturn(true);

        JournalEntry updatedEntry = JournalEntry.builder()
                .id("test-id-123")
                .date(testDate)
                .waterIntake(10)
                .exerciseMinutes(45)
                .mood("Energetic")
                .todos(new ArrayList<>())
                .notes("Updated notes")
                .build();

        // Act
        JournalEntry result = service.updateEntry("test-id-123", updatedEntry);

        // Assert
        assertEquals("test-id-123", result.getId());
        assertNotNull(result.getTimestamp());
        verify(repository, times(1)).save(any(JournalEntry.class));
    }

    @Test
    void testUpdateEntry_WhenEntryNotFound_ThenThrowsException() {
        // Arrange
        when(repository.existsById("non-existent")).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, 
            () -> service.updateEntry("non-existent", testEntry));
    }

    @Test
    void testDeleteEntry_WhenEntryExists_ThenReturnsTrue() {
        // Arrange
        when(repository.deleteById("test-id-123")).thenReturn(true);

        // Act
        boolean result = service.deleteEntry("test-id-123");

        // Assert
        assertTrue(result);
        verify(repository, times(1)).deleteById("test-id-123");
    }

    @Test
    void testDeleteEntry_WhenEntryNotFound_ThenReturnsFalse() {
        // Arrange
        when(repository.deleteById("non-existent")).thenReturn(false);

        // Act
        boolean result = service.deleteEntry("non-existent");

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetEntryById_WhenEntryExists_ThenReturnsEntry() {
        // Arrange
        when(repository.findById("test-id-123")).thenReturn(Optional.of(testEntry));

        // Act
        Optional<JournalEntry> result = service.getEntryById("test-id-123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test-id-123", result.get().getId());
    }

    @Test
    void testGetEntryById_WhenEntryNotFound_ThenReturnsEmpty() {
        // Arrange
        when(repository.findById("non-existent")).thenReturn(Optional.empty());

        // Act
        Optional<JournalEntry> result = service.getEntryById("non-existent");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllEntries_WhenEntriesExist_ThenReturnsAllEntries() {
        // Arrange
        List<JournalEntry> entries = List.of(testEntry);
        when(repository.findAll()).thenReturn(entries);

        // Act
        List<JournalEntry> result = service.getAllEntries();

        // Assert
        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetEntriesForDate_WhenEntriesExist_ThenReturnsEntries() {
        // Arrange
        List<JournalEntry> entries = List.of(testEntry);
        when(repository.findByDate(testDate)).thenReturn(entries);

        // Act
        List<JournalEntry> result = service.getEntriesForDate(testDate);

        // Assert
        assertEquals(1, result.size());
        verify(repository, times(1)).findByDate(testDate);
    }

    @Test
    void testGetEntriesForDateRange_WhenValidRange_ThenReturnsEntries() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 2, 1);
        LocalDate endDate = LocalDate.of(2026, 2, 28);
        List<JournalEntry> entries = List.of(testEntry);
        when(repository.findByDateRange(startDate, endDate)).thenReturn(entries);

        // Act
        List<JournalEntry> result = service.getEntriesForDateRange(startDate, endDate);

        // Assert
        assertEquals(1, result.size());
        verify(repository, times(1)).findByDateRange(startDate, endDate);
    }

    @Test
    void testGetEntriesForDateRange_WhenStartAfterEnd_ThenThrowsException() {
        // Arrange
        LocalDate startDate = LocalDate.of(2026, 2, 28);
        LocalDate endDate = LocalDate.of(2026, 2, 1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> service.getEntriesForDateRange(startDate, endDate));
    }

    @Test
    void testSearchEntries_WhenQueryMatchesNotes_ThenReturnsMatches() {
        // Arrange
        List<JournalEntry> allEntries = List.of(testEntry);
        when(repository.findAll()).thenReturn(allEntries);

        // Act
        List<JournalEntry> result = service.searchEntries("Test notes");

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testSearchEntries_WhenQueryMatchesMood_ThenReturnsMatches() {
        // Arrange
        List<JournalEntry> allEntries = List.of(testEntry);
        when(repository.findAll()).thenReturn(allEntries);

        // Act
        List<JournalEntry> result = service.searchEntries("Happy");

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testSearchEntries_WhenEmptyQuery_ThenReturnsAllEntries() {
        // Arrange
        List<JournalEntry> allEntries = List.of(testEntry);
        when(repository.findAll()).thenReturn(allEntries);

        // Act
        List<JournalEntry> result = service.searchEntries("");

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testSearchEntries_WhenNoMatches_ThenReturnsEmptyList() {
        // Arrange
        List<JournalEntry> allEntries = List.of(testEntry);
        when(repository.findAll()).thenReturn(allEntries);

        // Act
        List<JournalEntry> result = service.searchEntries("nonexistent");

        // Assert
        assertEquals(0, result.size());
    }
}

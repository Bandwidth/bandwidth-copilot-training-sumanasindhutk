package com.journal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a daily journal entry with comprehensive tracking features.
 * Includes water intake, exercise, mood, to-do items, and notes.
 * Stored as individual JSON files in the file system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry {

    /**
     * Unique identifier for the journal entry.
     */
    private String id;

    /**
     * Date of the journal entry.
     */
    @NotNull(message = "Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * Timestamp when the entry was created or last updated.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Water intake in milliliters or glasses.
     */
    @Min(value = 0, message = "Water intake cannot be negative")
    private int waterIntake;

    /**
     * Exercise duration in minutes.
     */
    @Min(value = 0, message = "Exercise minutes cannot be negative")
    private int exerciseMinutes;

    /**
     * Mood for the day (e.g., Happy, Neutral, Sad, Stressed, Energetic).
     */
    private String mood;

    /**
     * List of to-do items for the day.
     */
    @Valid
    @Builder.Default
    private List<TodoItem> todos = new ArrayList<>();

    /**
     * Free-form notes supporting rich text or markdown.
     */
    private String notes;

    /**
     * Creates a new JournalEntry with generated ID and current timestamp.
     *
     * @param date The date for this entry
     * @return A new JournalEntry instance
     */
    public static JournalEntry create(LocalDate date) {
        return JournalEntry.builder()
                .id(UUID.randomUUID().toString())
                .date(date)
                .timestamp(LocalDateTime.now())
                .waterIntake(0)
                .exerciseMinutes(0)
                .mood("Neutral")
                .todos(new ArrayList<>())
                .notes("")
                .build();
    }

    /**
     * Updates the timestamp to the current time.
     * Called when the entry is modified.
     */
    public void updateTimestamp() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Adds a to-do item to this entry.
     *
     * @param todoItem The to-do item to add
     */
    public void addTodo(TodoItem todoItem) {
        if (this.todos == null) {
            this.todos = new ArrayList<>();
        }
        this.todos.add(todoItem);
    }

    /**
     * Removes a to-do item by ID.
     *
     * @param todoId The ID of the to-do item to remove
     * @return true if the item was removed, false otherwise
     */
    public boolean removeTodo(String todoId) {
        if (this.todos == null) {
            return false;
        }
        return this.todos.removeIf(todo -> todo.getId().equals(todoId));
    }
}

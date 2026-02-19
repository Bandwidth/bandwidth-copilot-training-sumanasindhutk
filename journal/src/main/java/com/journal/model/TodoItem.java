package com.journal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Represents a single to-do item within a journal entry.
 * Each item has a description, completion status, and priority (1-5 stars).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoItem {

    /**
     * Unique identifier for the to-do item.
     */
    private String id;

    /**
     * Description of the to-do item.
     */
    @NotBlank(message = "To-do description cannot be blank")
    private String description;

    /**
     * Whether the to-do item is completed.
     */
    private boolean completed;

    /**
     * Priority level (1-5 stars).
     * 1 = Lowest priority, 5 = Highest priority
     */
    @Min(value = 1, message = "Priority must be at least 1")
    @Max(value = 5, message = "Priority must be at most 5")
    private int priority;

    /**
     * Creates a new TodoItem with a generated UUID.
     *
     * @param description The description of the to-do item
     * @param priority The priority level (1-5)
     */
    public static TodoItem create(String description, int priority) {
        return TodoItem.builder()
                .id(UUID.randomUUID().toString())
                .description(description)
                .completed(false)
                .priority(priority)
                .build();
    }
}

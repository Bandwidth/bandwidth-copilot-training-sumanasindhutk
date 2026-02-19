package com.coveros.training.flavorhub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an ingredient that can be used in recipes
 */
@Entity
@Table(name = "ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Ingredient name is required")
    @Size(min = 2, max = 100, message = "Ingredient name must be between 2 and 100 characters")
    @Column(nullable = false, unique = true)
    private String name;
    
    @Size(max = 50, message = "Category must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-&]+$", message = "Category must contain only letters, numbers, spaces, hyphens, and ampersands")
    @Column(name = "category")
    private String category; // e.g., "Dairy", "Vegetable", "Spice", "Meat", "Nuts & Seeds"
    
    @Size(max = 30, message = "Unit must not exceed 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-]+$", message = "Unit must contain only letters, numbers, spaces, and hyphens")
    @Column(name = "unit")
    private String unit; // e.g., "cups", "tablespoons", "grams", "fluid-ounces"
    
    public Ingredient(String name, String category, String unit) {
        this.name = name;
        this.category = category;
        this.unit = unit;
    }
}

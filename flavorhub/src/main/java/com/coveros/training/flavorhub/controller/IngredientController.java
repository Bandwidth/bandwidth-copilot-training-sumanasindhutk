package com.coveros.training.flavorhub.controller;

import com.coveros.training.flavorhub.model.Ingredient;
import com.coveros.training.flavorhub.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * REST Controller for managing ingredients
 * Uses Spring validation annotations to enforce input constraints
 */
@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Validated
public class IngredientController {
    
    private final IngredientService ingredientService;
    
    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        return ResponseEntity.ok(ingredientService.getAllIngredients());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(
            @PathVariable 
            @Positive(message = "Ingredient ID must be a positive number")
            Long id) {
        return ingredientService.getIngredientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Search ingredients by name
     * Validates that the search query is not blank and within acceptable length
     * 
     * @param query The search term (required, 2-100 characters)
     * @return List of ingredients matching the search query
     */
    @GetMapping("/search")
    public ResponseEntity<List<Ingredient>> searchIngredients(
            @RequestParam 
            @NotBlank(message = "Search query cannot be blank")
            @Size(min = 2, max = 100, message = "Search query must be between 2 and 100 characters")
            String query) {
        return ResponseEntity.ok(ingredientService.searchIngredients(query));
    }
    
    /**
     * Get ingredients by category
     * Validates that the category is not blank and contains only allowed characters
     * 
     * @param category The ingredient category (required, letters/numbers/spaces/hyphens/ampersands only, max 50 chars)
     * @return List of ingredients in the specified category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Ingredient>> getIngredientsByCategory(
            @PathVariable 
            @NotBlank(message = "Category cannot be blank")
            @Size(max = 50, message = "Category must not exceed 50 characters")
            @Pattern(regexp = "^[a-zA-Z0-9\\s\\-&]+$", message = "Category must contain only letters, numbers, spaces, hyphens, and ampersands")
            String category) {
        return ResponseEntity.ok(ingredientService.getIngredientsByCategory(category));
    }
    
    @PostMapping
    public ResponseEntity<Ingredient> createIngredient(@Valid @RequestBody Ingredient ingredient) {
        Ingredient saved = ingredientService.saveIngredient(ingredient);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(
            @PathVariable Long id, 
            @Valid @RequestBody Ingredient ingredient) {
        return ingredientService.getIngredientById(id)
                .map(existing -> {
                    ingredient.setId(id);
                    return ResponseEntity.ok(ingredientService.saveIngredient(ingredient));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}

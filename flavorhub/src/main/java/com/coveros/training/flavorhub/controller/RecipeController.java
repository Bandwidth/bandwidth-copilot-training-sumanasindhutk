package com.coveros.training.flavorhub.controller;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * REST Controller for managing recipes
 * Uses Spring validation annotations to enforce input constraints
 */
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Validated
public class RecipeController {
    
    private final RecipeService recipeService;
    
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(
            @PathVariable 
            @Positive(message = "Recipe ID must be a positive number")
            Long id) {
        return recipeService.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get the recipe of the day
     * Returns a deterministically selected recipe based on the current date
     * The same recipe is shown all day and changes daily
     * 
     * @return The recipe of the day with all details
     * @throws IllegalStateException if no recipes are available
     */
    @GetMapping("/daily")
    public ResponseEntity<Recipe> getRecipeOfTheDay() {
        try {
            Recipe recipeOfDay = recipeService.getRecipeOfTheDay();
            return ResponseEntity.ok(recipeOfDay);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Search recipes by name
     * Validates that the search query is not blank and within acceptable length
     * 
     * @param query The search term (required, 2-100 characters)
     * @return List of recipes matching the search query
     * @throws jakarta.validation.ConstraintViolationException if query validation fails
     */
    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(
            @RequestParam 
            @NotBlank(message = "Search query cannot be blank")
            @Size(min = 2, max = 100, message = "Search query must be between 2 and 100 characters")
            String query) {
        return ResponseEntity.ok(recipeService.searchRecipes(query));
    }
    
    /**
     * Get recipes by difficulty level
     * NOTE: Workshop participants will implement this endpoint using Copilot
     */
    // TODO: Implement GET /api/recipes/difficulty/{level} endpoint
    
    /**
     * Get recipes by cuisine type
     * NOTE: Workshop participants will implement this endpoint using Copilot
     */
    // TODO: Implement GET /api/recipes/cuisine/{type} endpoint
    
    /**
     * Recommend recipes based on available pantry ingredients
     * NOTE: This is an advanced endpoint to be implemented during the workshop
     */
    // TODO: Implement GET /api/recipes/recommendations endpoint
    
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody Recipe recipe) {
        Recipe saved = recipeService.saveRecipe(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable 
            @Positive(message = "Recipe ID must be a positive number")
            Long id, 
            @Valid @RequestBody Recipe recipe) {
        return recipeService.getRecipeById(id)
                .map(existing -> {
                    recipe.setId(id);
                    return ResponseEntity.ok(recipeService.saveRecipe(recipe));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable 
            @Positive(message = "Recipe ID must be a positive number")
            Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}

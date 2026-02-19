package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing recipes
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecipeService {
    
    private final RecipeRepository recipeRepository;
    
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
    
    /**
     * Get a recipe by ID
     * 
     * @param id The recipe ID (must be positive)
     * @return Optional containing the recipe if found
     */
    public Optional<Recipe> getRecipeById(
            @Positive(message = "Recipe ID must be a positive number")
            Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Recipe ID must be a positive number");
        }
        return recipeRepository.findById(id);
    }
    
    /**
     * Get recipes by difficulty level
     * 
     * @param difficultyLevel The difficulty level (must not be blank)
     * @return List of recipes matching the difficulty level
     */
    public List<Recipe> getRecipesByDifficulty(
            @NotBlank(message = "Difficulty level cannot be blank")
            @Size(max = 50, message = "Difficulty level must not exceed 50 characters")
            String difficultyLevel) {
        if (difficultyLevel == null || difficultyLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Difficulty level cannot be null or blank");
        }
        return recipeRepository.findByDifficultyLevel(difficultyLevel.trim());
    }
    
    /**
     * Get recipes by cuisine type
     * 
     * @param cuisineType The cuisine type (must not be blank)
     * @return List of recipes matching the cuisine type
     */
    public List<Recipe> getRecipesByCuisine(
            @NotBlank(message = "Cuisine type cannot be blank")
            @Size(max = 50, message = "Cuisine type must not exceed 50 characters")
            String cuisineType) {
        if (cuisineType == null || cuisineType.trim().isEmpty()) {
            throw new IllegalArgumentException("Cuisine type cannot be null or blank");
        }
        return recipeRepository.findByCuisineType(cuisineType.trim());
    }
    
    /**
     * Search for recipes by name
     * Validates input and trims whitespace before executing the search
     * 
     * @param searchTerm The search query to find recipes by name (required, 2-100 characters)
     * @return List of recipes matching the search term (case-insensitive)
     * @throws IllegalArgumentException if searchTerm is null or blank after trimming
     * @throws jakarta.validation.ConstraintViolationException if validation constraints are violated
     */
    public List<Recipe> searchRecipes(
            @NotBlank(message = "Search term cannot be null or blank")
            @Size(min = 2, max = 100, message = "Search term must be between 2 and 100 characters")
            String searchTerm) {
        // Defensive programming: validate and trim input
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be null or blank");
        }
        
        String trimmedSearchTerm = searchTerm.trim();
        
        // Validate minimum length after trimming
        if (trimmedSearchTerm.length() < 2) {
            throw new IllegalArgumentException("Search term must be at least 2 characters long");
        }
        
        // Execute search with case-insensitive matching
        return recipeRepository.findByNameContainingIgnoreCase(trimmedSearchTerm);
    }
    
    /**
     * Get the recipe of the day
     * Uses a deterministic algorithm based on the current date to ensure
     * the same recipe is shown all day and varies by day
     * 
     * @return The recipe of the day
     * @throws IllegalStateException if no recipes are available
     */
    public Recipe getRecipeOfTheDay() {
        List<Recipe> allRecipes = recipeRepository.findAll();
        
        if (allRecipes.isEmpty()) {
            log.warn("No recipes available for recipe of the day");
            throw new IllegalStateException("No recipes available");
        }
        
        // Use day of year as a deterministic seed
        // This ensures the same recipe is shown all day
        int dayOfYear = LocalDate.now().getDayOfYear();
        int recipeIndex = dayOfYear % allRecipes.size();
        
        Recipe recipeOfDay = allRecipes.get(recipeIndex);
        log.info("Recipe of the day selected: {} (Day: {}, Index: {})", 
            recipeOfDay.getName(), dayOfYear, recipeIndex);
        
        return recipeOfDay;
    }
    
    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
    
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }
    
    /**
     * Find recipes that can be made based on available ingredients in the pantry
     * NOTE: This method is intentionally left incomplete for workshop participants
     * Participants will use GitHub Copilot to implement this recommendation logic
     */
    // TODO: Implement method to recommend recipes based on pantry ingredients
    
    /**
     * Get recipes that match specific dietary requirements or filters
     * NOTE: This is a more advanced feature to be implemented during the workshop
     */
    // TODO: Implement advanced filtering logic
}

package com.coveros.training.flavorhub.controller;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for managing seasonal recipes through the pantry manager
 * Provides endpoints to discover recipes based on seasonal availability
 * 
 * @author FlavorHub Team
 */
@RestController
@RequestMapping("/api/pantry-manager")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PantryManagerController {
    
    private final RecipeService recipeService;
    
    /**
     * Determines the current season based on the current date
     * 
     * @return The current season (Spring, Summer, Fall, or Winter)
     */
    private String getCurrentSeason() {
        Month month = LocalDate.now().getMonth();
        
        if (month == Month.MARCH || month == Month.APRIL || month == Month.MAY) {
            return "Spring";
        } else if (month == Month.JUNE || month == Month.JULY || month == Month.AUGUST) {
            return "Summer";
        } else if (month == Month.SEPTEMBER || month == Month.OCTOBER || month == Month.NOVEMBER) {
            return "Fall";
        } else {
            return "Winter";
        }
    }
    
    /**
     * Get seasonal recipes based on the current season
     * Returns recipes that are appropriate for the current time of year
     * 
     * @return List of recipes suitable for the current season
     */
    @GetMapping("/seasonal-recipes")
    public ResponseEntity<List<Recipe>> getCurrentSeasonalRecipes() {
        String currentSeason = getCurrentSeason();
        log.info("Fetching recipes for current season: {}", currentSeason);
        
        List<Recipe> allRecipes = recipeService.getAllRecipes();
        List<Recipe> seasonalRecipes = filterRecipesBySeason(allRecipes, currentSeason);
        
        log.info("Found {} recipes for {} season", seasonalRecipes.size(), currentSeason);
        return ResponseEntity.ok(seasonalRecipes);
    }
    
    /**
     * Get recipes by a specific season
     * 
     * @param season The season to filter by (Spring, Summer, Fall, Winter) - case insensitive
     * @return List of recipes suitable for the specified season
     * @throws jakarta.validation.ConstraintViolationException if season validation fails
     */
    @GetMapping("/seasonal-recipes/{season}")
    public ResponseEntity<List<Recipe>> getRecipesBySeason(
            @PathVariable
            @NotBlank(message = "Season cannot be blank")
            @Pattern(regexp = "(?i)(spring|summer|fall|winter)", 
                    message = "Season must be one of: Spring, Summer, Fall, Winter")
            String season) {
        
        String normalizedSeason = normalizeSeason(season);
        log.info("Fetching recipes for season: {}", normalizedSeason);
        
        List<Recipe> allRecipes = recipeService.getAllRecipes();
        List<Recipe> seasonalRecipes = filterRecipesBySeason(allRecipes, normalizedSeason);
        
        if (seasonalRecipes.isEmpty()) {
            log.warn("No recipes found for season: {}", normalizedSeason);
        }
        
        return ResponseEntity.ok(seasonalRecipes);
    }
    
    /**
     * Get the current season
     * 
     * @return The current season name
     */
    @GetMapping("/current-season")
    public ResponseEntity<String> getCurrentSeasonName() {
        String season = getCurrentSeason();
        log.info("Current season is: {}", season);
        return ResponseEntity.ok(season);
    }
    
    /**
     * Filter recipes based on season
     * Uses cuisine type and description as indicators of seasonal appropriateness
     * 
     * @param recipes The list of all recipes
     * @param season The season to filter by
     * @return Filtered list of seasonal recipes
     */
    private List<Recipe> filterRecipesBySeason(List<Recipe> recipes, String season) {
        return recipes.stream()
                .filter(recipe -> isRecipeSuitableForSeason(recipe, season))
                .collect(Collectors.toList());
    }
    
    /**
     * Determines if a recipe is suitable for the given season
     * Based on cuisine type, description keywords, and recipe characteristics
     * 
     * @param recipe The recipe to evaluate
     * @param season The season to check against
     * @return true if the recipe is suitable for the season
     */
    private boolean isRecipeSuitableForSeason(Recipe recipe, String season) {
        String description = recipe.getDescription() != null ? recipe.getDescription().toLowerCase() : "";
        String cuisineType = recipe.getCuisineType() != null ? recipe.getCuisineType().toLowerCase() : "";
        String name = recipe.getName() != null ? recipe.getName().toLowerCase() : "";
        
        switch (season) {
            case "Spring":
                // Light, fresh ingredients common in spring
                return description.contains("spring") || 
                       description.contains("fresh") || 
                       description.contains("light") ||
                       name.contains("salad") ||
                       name.contains("spring") ||
                       cuisineType.contains("mediterranean");
                       
            case "Summer":
                // Grilled, refreshing, cold dishes
                return description.contains("summer") || 
                       description.contains("grilled") || 
                       description.contains("cold") ||
                       description.contains("refreshing") ||
                       name.contains("summer") ||
                       name.contains("bbq") ||
                       name.contains("salad");
                       
            case "Fall":
                // Warm, hearty, comfort foods
                return description.contains("fall") || 
                       description.contains("autumn") || 
                       description.contains("hearty") ||
                       description.contains("pumpkin") ||
                       description.contains("roasted") ||
                       name.contains("fall") ||
                       name.contains("pumpkin") ||
                       name.contains("stew");
                       
            case "Winter":
                // Hot, warming, substantial meals
                return description.contains("winter") || 
                       description.contains("warm") || 
                       description.contains("hot") ||
                       description.contains("comfort") ||
                       description.contains("soup") ||
                       name.contains("winter") ||
                       name.contains("soup") ||
                       name.contains("stew") ||
                       cuisineType.contains("italian"); // Pasta dishes popular in winter
                       
            default:
                // If season doesn't match, return all recipes
                return true;
        }
    }
    
    /**
     * Normalize season name to proper case
     * 
     * @param season The season input from user
     * @return Normalized season name
     */
    private String normalizeSeason(String season) {
        return season.substring(0, 1).toUpperCase() + season.substring(1).toLowerCase();
    }
}

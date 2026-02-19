package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.model.Ingredient;
import com.coveros.training.flavorhub.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing ingredients
 */
@Service
@RequiredArgsConstructor
@Transactional
public class IngredientService {
    
    private final IngredientRepository ingredientRepository;
    
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }
    
    public Optional<Ingredient> getIngredientById(Long id) {
        return ingredientRepository.findById(id);
    }
    
    public Optional<Ingredient> getIngredientByName(
            @NotBlank(message = "Ingredient name cannot be blank")
            @Size(min = 2, max = 100, message = "Ingredient name must be between 2 and 100 characters")
            String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or blank");
        }
        return ingredientRepository.findByNameIgnoreCase(name.trim());
    }
    
    /**
     * Get ingredients by category
     * Validates input and trims whitespace before executing the query
     * 
     * @param category The ingredient category (required, 1-50 characters, letters/numbers/spaces/hyphens/ampersands)
     * @return List of ingredients in the specified category
     */
    public List<Ingredient> getIngredientsByCategory(
            @NotBlank(message = "Category cannot be blank")
            @Size(min = 1, max = 50, message = "Category must be between 1 and 50 characters")
            @Pattern(regexp = "^[a-zA-Z0-9\\s\\-&]+$", message = "Category must contain only letters, numbers, spaces, hyphens, and ampersands")
            String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or blank");
        }
        return ingredientRepository.findByCategory(category.trim());
    }
    
    /**
     * Search for ingredients by name
     * Validates input and trims whitespace before executing the search
     * 
     * @param searchTerm The search query (required, 2-100 characters)
     * @return List of ingredients matching the search term (case-insensitive)
     */
    public List<Ingredient> searchIngredients(
            @NotBlank(message = "Search term cannot be null or blank")
            @Size(min = 2, max = 100, message = "Search term must be between 2 and 100 characters")
            String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be null or blank");
        }
        
        String trimmedSearchTerm = searchTerm.trim();
        
        if (trimmedSearchTerm.length() < 2) {
            throw new IllegalArgumentException("Search term must be at least 2 characters long");
        }
        
        return ingredientRepository.findByNameContainingIgnoreCase(trimmedSearchTerm);
    }
    
    public Ingredient saveIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }
    
    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
    }
}

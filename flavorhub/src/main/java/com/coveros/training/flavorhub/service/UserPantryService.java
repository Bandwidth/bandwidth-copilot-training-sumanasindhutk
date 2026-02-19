package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.model.Ingredient;
import com.coveros.training.flavorhub.model.UserPantry;
import com.coveros.training.flavorhub.repository.IngredientRepository;
import com.coveros.training.flavorhub.repository.UserPantryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing user pantry
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserPantryService {
    
    private final UserPantryRepository userPantryRepository;
    private final IngredientRepository ingredientRepository;
    
    /**
     * Get all pantry items for a specific user
     * 
     * @param userId The user ID (must be positive)
     * @return List of pantry items for the user
     */
    public List<UserPantry> getUserPantry(
            @Positive(message = "User ID must be a positive number")
            Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        return userPantryRepository.findByUserId(userId);
    }
    
    /**
     * Get a specific pantry item by ID
     * 
     * @param id The pantry item ID (must be positive)
     * @return Optional containing the pantry item if found
     */
    public Optional<UserPantry> getPantryItemById(
            @Positive(message = "Pantry item ID must be a positive number")
            Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Pantry item ID must be a positive number");
        }
        return userPantryRepository.findById(id);
    }
    
    public UserPantry addPantryItem(UserPantry pantryItem) {
        if (pantryItem == null) {
            throw new IllegalArgumentException("Pantry item cannot be null");
        }
        if (pantryItem.getUserId() == null || pantryItem.getUserId() <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        if (pantryItem.getIngredient() == null) {
            throw new IllegalArgumentException("Ingredient is required");
        }
        return userPantryRepository.save(pantryItem);
    }
    
    /**
     * Update quantity of a pantry item
     * Validates that the updated quantities are positive
     * 
     * @param id The pantry item ID (must be positive)
     * @param updatedPantryItem The updated pantry item with new values
     * @return Updated pantry item
     * @throws IllegalArgumentException if id is invalid or item not found
     */
    public UserPantry updatePantryItem(
            @Positive(message = "Pantry item ID must be a positive number")
            Long id,
            @NotNull(message = "Updated pantry item cannot be null")
            UserPantry updatedPantryItem) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Pantry item ID must be a positive number");
        }
        
        if (updatedPantryItem.getQuantity() != null && updatedPantryItem.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }
        
        return userPantryRepository.findById(id)
            .map(existing -> {
                existing.setQuantity(updatedPantryItem.getQuantity());
                existing.setUnit(updatedPantryItem.getUnit());
                existing.setNotes(updatedPantryItem.getNotes());
                return userPantryRepository.save(existing);
            })
            .orElseThrow(() -> new IllegalArgumentException("Pantry item not found with id: " + id));
    }
    
    /**
     * Delete a pantry item
     * 
     * @param id The pantry item ID (must be positive)
     */
    public void deletePantryItem(
            @Positive(message = "Pantry item ID must be a positive number")
            Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Pantry item ID must be a positive number");
        }
        userPantryRepository.deleteById(id);
    }
    
    /**
     * Clear all items from user's pantry
     * 
     * @param userId The user ID (must be positive)
     */
    public void clearUserPantry(
            @Positive(message = "User ID must be a positive number")
            Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        userPantryRepository.deleteByUserId(userId);
    }
    
    /**
     * Check if user has sufficient quantity of an ingredient
     * NOTE: This method is intentionally left incomplete for workshop participants
     */
    // TODO: Implement method to check if user has enough of an ingredient
    
    /**
     * Get list of ingredient names that user has in pantry
     * NOTE: Workshop participants will implement this using Copilot
     */
    // TODO: Implement method to get ingredient names from user's pantry
}

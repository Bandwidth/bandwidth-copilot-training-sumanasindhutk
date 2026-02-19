package com.coveros.training.flavorhub.service;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RecipeService
 * Tests all public methods with positive, negative, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {
    
    @Mock
    private RecipeRepository recipeRepository;
    
    @InjectMocks
    private RecipeService recipeService;
    
    private Recipe testRecipe;
    private Recipe testRecipe2;
    
    @BeforeEach
    void setUp() {
        testRecipe = new Recipe("Pasta", "Italian pasta dish", 10, 15, 4, "Easy", "Italian");
        testRecipe.setId(1L);
        
        testRecipe2 = new Recipe("Tacos", "Mexican tacos", 5, 10, 2, "Easy", "Mexican");
        testRecipe2.setId(2L);
    }
    
    // ========== getAllRecipes() Tests ==========
    
    @Test
    void testGetAllRecipes_WhenRecipesExist_ThenReturnsAllRecipes() {
        // Arrange
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe, testRecipe2);
        when(recipeRepository.findAll()).thenReturn(expectedRecipes);
        
        // Act
        List<Recipe> result = recipeService.getAllRecipes();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Pasta", result.get(0).getName());
        assertEquals("Tacos", result.get(1).getName());
        verify(recipeRepository).findAll();
    }
    
    @Test
    void testGetAllRecipes_WhenNoRecipesExist_ThenReturnsEmptyList() {
        // Arrange
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());
        
        // Act
        List<Recipe> result = recipeService.getAllRecipes();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findAll();
    }
    
    // ========== getRecipeById() Tests ==========
    
    @Test
    void testGetRecipeById_WhenRecipeExists_ThenReturnsRecipe() {
        // Arrange
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        
        // Act
        Optional<Recipe> result = recipeService.getRecipeById(1L);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("Pasta", result.get().getName());
        assertEquals(1L, result.get().getId());
        verify(recipeRepository).findById(1L);
    }
    
    @Test
    void testGetRecipeById_WhenRecipeDoesNotExist_ThenReturnsEmpty() {
        // Arrange
        when(recipeRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act
        Optional<Recipe> result = recipeService.getRecipeById(999L);
        
        // Assert
        assertFalse(result.isPresent());
        verify(recipeRepository).findById(999L);
    }
    
    @Test
    void testGetRecipeById_WhenIdIsNull_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> recipeService.getRecipeById(null)
        );
        
        assertEquals("Recipe ID must be a positive number", exception.getMessage());
        verify(recipeRepository, never()).findById(any());
    }
    
    @Test
    void testGetRecipeById_WhenIdIsZero_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> recipeService.getRecipeById(0L)
        );
        
        assertEquals("Recipe ID must be a positive number", exception.getMessage());
        verify(recipeRepository, never()).findById(any());
    }
    
    @Test
    void testGetRecipeById_WhenIdIsNegative_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> recipeService.getRecipeById(-1L)
        );
        
        assertEquals("Recipe ID must be a positive number", exception.getMessage());
        verify(recipeRepository, never()).findById(any());
    }
    
    // ========== getRecipesByDifficulty() Tests ==========
    
    @Test
    void testGetRecipesByDifficulty_WhenRecipesExist_ThenReturnsMatchingRecipes() {
        // Arrange
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe, testRecipe2);
        when(recipeRepository.findByDifficultyLevel("Easy")).thenReturn(expectedRecipes);
        
        // Act
        List<Recipe> result = recipeService.getRecipesByDifficulty("Easy");
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Easy", result.get(0).getDifficultyLevel());
        verify(recipeRepository).findByDifficultyLevel("Easy");
    }
    
    @Test
    void testGetRecipesByDifficulty_WhenNoMatchingRecipes_ThenReturnsEmptyList() {
        // Arrange
        when(recipeRepository.findByDifficultyLevel("Hard")).thenReturn(Collections.emptyList());
        
        // Act
        List<Recipe> result = recipeService.getRecipesByDifficulty("Hard");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findByDifficultyLevel("Hard");
    }
    
    @Test
    void testGetRecipesByDifficulty_WhenDifficultyLevelIsNull_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> recipeService.getRecipesByDifficulty(null)
        );
        
        assertEquals("Difficulty level cannot be null or blank", exception.getMessage());
        verify(recipeRepository, never()).findByDifficultyLevel(any());
    }
    
    @Test
    void testGetRecipesByDifficulty_WhenDifficultyLevelIsBlank_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> recipeService.getRecipesByDifficulty("   ")
        );
        
        assertEquals("Difficulty level cannot be null or blank", exception.getMessage());
        verify(recipeRepository, never()).findByDifficultyLevel(any());
    }
    
    @Test
    void testGetRecipesByDifficulty_WhenDifficultyLevelHasWhitespace_ThenTrimsAndSearches() {
        // Arrange
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe);
        when(recipeRepository.findByDifficultyLevel("Easy")).thenReturn(expectedRecipes);
        
        // Act
        List<Recipe> result = recipeService.getRecipesByDifficulty("  Easy  ");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipeRepository).findByDifficultyLevel("Easy");
    }
    
    // ========== getRecipesByCuisine() Tests ==========
    
    @Test
    void testGetRecipesByCuisine_WhenRecipesExist_ThenReturnsMatchingRecipes() {
        // Arrange
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe);
        when(recipeRepository.findByCuisineType("Italian")).thenReturn(expectedRecipes);
        
        // Act
        List<Recipe> result = recipeService.getRecipesByCuisine("Italian");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Italian", result.get(0).getCuisineType());
        verify(recipeRepository).findByCuisineType("Italian");
    }
    
    @Test
    void testGetRecipesByCuisine_WhenNoMatchingRecipes_ThenReturnsEmptyList() {
        // Arrange
        when(recipeRepository.findByCuisineType("Chinese")).thenReturn(Collections.emptyList());
        
        // Act
        List<Recipe> result = recipeService.getRecipesByCuisine("Chinese");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findByCuisineType("Chinese");
    }
    
    @Test
    void testGetRecipesByCuisine_WhenCuisineTypeIsNull_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> recipeService.getRecipesByCuisine(null)
        );
        
        assertEquals("Cuisine type cannot be null or blank", exception.getMessage());
        verify(recipeRepository, never()).findByCuisineType(any());
    }
    
    @Test
    void testGetRecipesByCuisine_WhenCuisineTypeIsBlank_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> recipeService.getRecipesByCuisine("   ")
        );
        
        assertEquals("Cuisine type cannot be null or blank", exception.getMessage());
        verify(recipeRepository, never()).findByCuisineType(any());
    }
    
    @Test
    void testGetRecipesByCuisine_WhenCuisineTypeHasWhitespace_ThenTrimsAndSearches() {
        // Arrange
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe2);
        when(recipeRepository.findByCuisineType("Mexican")).thenReturn(expectedRecipes);
        
        // Act
        List<Recipe> result = recipeService.getRecipesByCuisine("  Mexican  ");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipeRepository).findByCuisineType("Mexican");
    }
    
    // ========== searchRecipes() Tests ==========
    
    @Test
    void testSearchRecipes_WhenMatchingRecipesExist_ThenReturnsMatchingRecipes() {
        // Arrange
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe);
        when(recipeRepository.findByNameContainingIgnoreCase("Pasta")).thenReturn(expectedRecipes);
        
        // Act
        List<Recipe> result = recipeService.searchRecipes("Pasta");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pasta", result.get(0).getName());
        verify(recipeRepository).findByNameContainingIgnoreCase("Pasta");
    }
    
    @Test
    void testSearchRecipes_WhenNoMatchingRecipes_ThenReturnsEmptyList() {
        // Arrange
        when(recipeRepository.findByNameContainingIgnoreCase("Pizza")).thenReturn(Collections.emptyList());
        
        // Act
        List<Recipe> result = recipeService.searchRecipes("Pizza");
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findByNameContainingIgnoreCase("Pizza");
    }
    
    @Test
    void testSearchRecipes_WhenSearchTermIsNull_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> recipeService.searchRecipes(null)
        );
        
        assertEquals("Search term cannot be null or blank", exception.getMessage());
        verify(recipeRepository, never()).findByNameContainingIgnoreCase(any());
    }
    
    @Test
    void testSearchRecipes_WhenSearchTermIsBlank_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> recipeService.searchRecipes("   ")
        );
        
        assertEquals("Search term cannot be null or blank", exception.getMessage());
        verify(recipeRepository, never()).findByNameContainingIgnoreCase(any());
    }
    
    @Test
    void testSearchRecipes_WhenSearchTermIsTooShort_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> recipeService.searchRecipes("a")
        );
        
        assertEquals("Search term must be at least 2 characters long", exception.getMessage());
        verify(recipeRepository, never()).findByNameContainingIgnoreCase(any());
    }
    
    @Test
    void testSearchRecipes_WhenSearchTermHasWhitespace_ThenTrimsAndSearches() {
        // Arrange
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe2);
        when(recipeRepository.findByNameContainingIgnoreCase("Tacos")).thenReturn(expectedRecipes);
        
        // Act
        List<Recipe> result = recipeService.searchRecipes("  Tacos  ");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipeRepository).findByNameContainingIgnoreCase("Tacos");
    }
    
    @Test
    void testSearchRecipes_WhenSearchTermHasWhitespaceAndBecomesTooShort_ThenThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> recipeService.searchRecipes("  a  ")
        );
        
        assertEquals("Search term must be at least 2 characters long", exception.getMessage());
        verify(recipeRepository, never()).findByNameContainingIgnoreCase(any());
    }
    
    @Test
    void testSearchRecipes_WhenSearchTermIsMinimumLength_ThenSearchesSuccessfully() {
        // Arrange
        List<Recipe> expectedRecipes = Arrays.asList(testRecipe);
        when(recipeRepository.findByNameContainingIgnoreCase("Pa")).thenReturn(expectedRecipes);
        
        // Act
        List<Recipe> result = recipeService.searchRecipes("Pa");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipeRepository).findByNameContainingIgnoreCase("Pa");
    }
    
    // ========== getRecipeOfTheDay() Tests ==========
    
    @Test
    void testGetRecipeOfTheDay_WhenRecipesExist_ThenReturnsDeterministicRecipe() {
        // Arrange
        List<Recipe> allRecipes = Arrays.asList(testRecipe, testRecipe2);
        when(recipeRepository.findAll()).thenReturn(allRecipes);
        
        // Act
        Recipe result = recipeService.getRecipeOfTheDay();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.getName().equals("Pasta") || result.getName().equals("Tacos"));
        verify(recipeRepository).findAll();
    }
    
    @Test
    void testGetRecipeOfTheDay_WhenCalledMultipleTimes_ThenReturnsSameRecipe() {
        // Arrange
        List<Recipe> allRecipes = Arrays.asList(testRecipe, testRecipe2);
        when(recipeRepository.findAll()).thenReturn(allRecipes);
        
        // Act
        Recipe result1 = recipeService.getRecipeOfTheDay();
        Recipe result2 = recipeService.getRecipeOfTheDay();
        
        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.getName(), result2.getName());
        verify(recipeRepository, times(2)).findAll();
    }
    
    @Test
    void testGetRecipeOfTheDay_WhenNoRecipesExist_ThenThrowsIllegalStateException() {
        // Arrange
        when(recipeRepository.findAll()).thenReturn(Collections.emptyList());
        
        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> recipeService.getRecipeOfTheDay()
        );
        
        assertEquals("No recipes available", exception.getMessage());
        verify(recipeRepository).findAll();
    }
    
    @Test
    void testGetRecipeOfTheDay_WhenOnlyOneRecipeExists_ThenReturnsAlwaysSameRecipe() {
        // Arrange
        List<Recipe> allRecipes = Arrays.asList(testRecipe);
        when(recipeRepository.findAll()).thenReturn(allRecipes);
        
        // Act
        Recipe result = recipeService.getRecipeOfTheDay();
        
        // Assert
        assertNotNull(result);
        assertEquals("Pasta", result.getName());
        verify(recipeRepository).findAll();
    }
    
    // ========== saveRecipe() Tests ==========
    
    @Test
    void testSaveRecipe_WhenValidRecipe_ThenSavesAndReturnsRecipe() {
        // Arrange
        when(recipeRepository.save(testRecipe)).thenReturn(testRecipe);
        
        // Act
        Recipe result = recipeService.saveRecipe(testRecipe);
        
        // Assert
        assertNotNull(result);
        assertEquals("Pasta", result.getName());
        assertEquals(1L, result.getId());
        verify(recipeRepository).save(testRecipe);
    }
    
    @Test
    void testSaveRecipe_WhenNewRecipe_ThenSavesAndReturnsRecipeWithId() {
        // Arrange
        Recipe newRecipe = new Recipe("Sushi", "Japanese sushi", 30, 0, 2, "Hard", "Japanese");
        Recipe savedRecipe = new Recipe("Sushi", "Japanese sushi", 30, 0, 2, "Hard", "Japanese");
        savedRecipe.setId(3L);
        when(recipeRepository.save(newRecipe)).thenReturn(savedRecipe);
        
        // Act
        Recipe result = recipeService.saveRecipe(newRecipe);
        
        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Sushi", result.getName());
        verify(recipeRepository).save(newRecipe);
    }
    
    // ========== deleteRecipe() Tests ==========
    
    @Test
    void testDeleteRecipe_WhenValidId_ThenDeletesRecipe() {
        // Arrange
        doNothing().when(recipeRepository).deleteById(1L);
        
        // Act
        recipeService.deleteRecipe(1L);
        
        // Assert
        verify(recipeRepository).deleteById(1L);
    }
    
    @Test
    void testDeleteRecipe_WhenCalledWithAnyId_ThenCallsRepositoryDelete() {
        // Arrange
        doNothing().when(recipeRepository).deleteById(999L);
        
        // Act
        recipeService.deleteRecipe(999L);
        
        // Assert
        verify(recipeRepository).deleteById(999L);
    }
}

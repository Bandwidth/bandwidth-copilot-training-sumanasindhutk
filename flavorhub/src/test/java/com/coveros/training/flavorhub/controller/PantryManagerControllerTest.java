package com.coveros.training.flavorhub.controller;

import com.coveros.training.flavorhub.model.Recipe;
import com.coveros.training.flavorhub.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PantryManagerController
 * Tests all endpoints with happy path, edge cases, and validation scenarios
 * Uses @WebMvcTest for controller layer testing with mocked dependencies
 */
@WebMvcTest(PantryManagerController.class)
class PantryManagerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private RecipeService recipeService;
    
    private Recipe springRecipe;
    private Recipe summerRecipe;
    private Recipe fallRecipe;
    private Recipe winterRecipe;
    private List<Recipe> allRecipes;
    
    @BeforeEach
    void setUp() {
        // Spring recipe - light and fresh
        springRecipe = new Recipe("Spring Salad", "A fresh and light salad perfect for spring", 
                10, 0, 4, "Easy", "Mediterranean");
        springRecipe.setId(1L);
        
        // Summer recipe - grilled
        summerRecipe = new Recipe("Summer BBQ Chicken", "Grilled chicken perfect for summer gatherings", 
                15, 30, 6, "Medium", "American");
        summerRecipe.setId(2L);
        
        // Fall recipe - hearty
        fallRecipe = new Recipe("Pumpkin Stew", "A hearty pumpkin stew for fall evenings", 
                20, 45, 4, "Medium", "American");
        fallRecipe.setId(3L);
        
        // Winter recipe - warm comfort food
        winterRecipe = new Recipe("Winter Pasta", "Warm Italian pasta perfect for winter comfort", 
                10, 20, 4, "Easy", "Italian");
        winterRecipe.setId(4L);
        
        allRecipes = Arrays.asList(springRecipe, summerRecipe, fallRecipe, winterRecipe);
    }
    
    // ========== getCurrentSeasonalRecipes() Tests ==========
    
    @Test
    void testGetCurrentSeasonalRecipes_WhenRecipesExist_ThenReturnsFilteredRecipes() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenReturn(allRecipes);
        
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
        
        verify(recipeService).getAllRecipes();
    }
    
    @Test
    void testGetCurrentSeasonalRecipes_WhenNoRecipes_ThenReturnsEmptyList() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenReturn(Collections.emptyList());
        
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        
        verify(recipeService).getAllRecipes();
    }
    
    // ========== getRecipesBySeason() Tests ==========
    
    @Test
    void testGetRecipesBySeason_WhenSeasonIsSpring_ThenReturnsSpringRecipes() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenReturn(allRecipes);
        
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/Spring")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name", hasItem("Spring Salad")));
        
        verify(recipeService).getAllRecipes();
    }
    
    @Test
    void testGetRecipesBySeason_WhenSeasonIsSummer_ThenReturnsSummerRecipes() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenReturn(allRecipes);
        
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/Summer")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name", hasItem("Summer BBQ Chicken")));
        
        verify(recipeService).getAllRecipes();
    }
    
    @Test
    void testGetRecipesBySeason_WhenSeasonIsFall_ThenReturnsFallRecipes() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenReturn(allRecipes);
        
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/Fall")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name", hasItem("Pumpkin Stew")));
        
        verify(recipeService).getAllRecipes();
    }
    
    @Test
    void testGetRecipesBySeason_WhenSeasonIsWinter_ThenReturnsWinterRecipes() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenReturn(allRecipes);
        
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/Winter")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name", hasItem("Winter Pasta")));
        
        verify(recipeService).getAllRecipes();
    }
    
    @Test
    void testGetRecipesBySeason_WhenSeasonIsLowercase_ThenReturnsRecipes() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenReturn(allRecipes);
        
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/spring")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
        
        verify(recipeService).getAllRecipes();
    }
    
    @Test
    void testGetRecipesBySeason_WhenSeasonIsUppercase_ThenReturnsRecipes() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenReturn(allRecipes);
        
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/SUMMER")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
        
        verify(recipeService).getAllRecipes();
    }
    
    @Test
    void testGetRecipesBySeason_WhenSeasonIsMixedCase_ThenReturnsRecipes() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenReturn(allRecipes);
        
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/FaLl")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
        
        verify(recipeService).getAllRecipes();
    }
    
    @Test
    void testGetRecipesBySeason_WhenSeasonIsInvalid_ThenReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/InvalidSeason")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        verify(recipeService, never()).getAllRecipes();
    }
    
    @Test
    void testGetRecipesBySeason_WhenSeasonIsEmpty_ThenReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/ ")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        verify(recipeService, never()).getAllRecipes();
    }
    
    @Test
    void testGetRecipesBySeason_WhenNoMatchingRecipes_ThenReturnsEmptyList() throws Exception {
        // Arrange
        Recipe noSeasonalRecipe = new Recipe("Generic Recipe", "A generic recipe", 
                10, 15, 4, "Easy", "Generic");
        noSeasonalRecipe.setId(5L);
        when(recipeService.getAllRecipes()).thenReturn(Collections.singletonList(noSeasonalRecipe));
        
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/Spring")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        
        verify(recipeService).getAllRecipes();
    }
    
    // ========== getCurrentSeasonName() Tests ==========
    
    @Test
    void testGetCurrentSeasonName_WhenCalled_ThenReturnsSeasonString() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/current-season")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                .andExpect(content().string(anyOf(
                        equalTo("Spring"), 
                        equalTo("Summer"), 
                        equalTo("Fall"), 
                        equalTo("Winter")
                )));
        
        verifyNoInteractions(recipeService);
    }
    
    @Test
    void testGetCurrentSeasonName_WhenCalledMultipleTimes_ThenReturnsSameSeasonForSameDay() throws Exception {
        // Act & Assert - Call twice and expect same result
        String firstResult = mockMvc.perform(get("/api/pantry-manager/current-season"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        String secondResult = mockMvc.perform(get("/api/pantry-manager/current-season"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // Both calls should return the same season
        assert firstResult.equals(secondResult);
        
        verifyNoInteractions(recipeService);
    }
    
    // ========== Integration/Edge Case Tests ==========
    
    @Test
    void testGetRecipesBySeason_WhenMultipleSeasonsMatch_ThenReturnsAllMatching() throws Exception {
        // Arrange - Create a recipe that matches multiple season criteria
        Recipe multiSeasonRecipe = new Recipe("Fresh Salad", "A fresh, light, grilled summer salad", 
                10, 5, 4, "Easy", "Mediterranean");
        multiSeasonRecipe.setId(6L);
        when(recipeService.getAllRecipes()).thenReturn(Collections.singletonList(multiSeasonRecipe));
        
        // Act & Assert - Should appear in both Spring and Summer results
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/Spring")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem("Fresh Salad")));
        
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/Summer")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem("Fresh Salad")));
        
        verify(recipeService, times(2)).getAllRecipes();
    }
    
    @Test
    void testGetRecipesBySeason_WhenRecipeHasNullFields_ThenHandlesGracefully() throws Exception {
        // Arrange - Recipe with null description and cuisineType
        Recipe nullFieldsRecipe = new Recipe();
        nullFieldsRecipe.setId(7L);
        nullFieldsRecipe.setName("Basic Recipe");
        nullFieldsRecipe.setPrepTime(10);
        nullFieldsRecipe.setCookTime(15);
        nullFieldsRecipe.setServings(4);
        nullFieldsRecipe.setDifficultyLevel("Easy");
        // description and cuisineType are null
        
        when(recipeService.getAllRecipes()).thenReturn(Collections.singletonList(nullFieldsRecipe));
        
        // Act & Assert - Should not throw NPE
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/Spring")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        
        verify(recipeService).getAllRecipes();
    }
    
    @Test
    void testGetRecipesBySeason_WhenServiceThrowsException_ThenReturnsInternalServerError() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenThrow(new RuntimeException("Database error"));
        
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/Spring")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        
        verify(recipeService).getAllRecipes();
    }
    
    @Test
    void testGetCurrentSeasonalRecipes_WhenServiceThrowsException_ThenReturnsInternalServerError() throws Exception {
        // Arrange
        when(recipeService.getAllRecipes()).thenThrow(new RuntimeException("Database error"));
        
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        
        verify(recipeService).getAllRecipes();
    }
    
    // ========== Validation Tests ==========
    
    @Test
    void testGetRecipesBySeason_WhenSeasonContainsSpecialCharacters_ThenReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/Spring!")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        verify(recipeService, never()).getAllRecipes();
    }
    
    @Test
    void testGetRecipesBySeason_WhenSeasonContainsNumbers_ThenReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/pantry-manager/seasonal-recipes/Spring123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        verify(recipeService, never()).getAllRecipes();
    }
}

package com.cuesina.services;

import com.cuesina.models.Ingredient;
import com.cuesina.models.Recipe;
import com.cuesina.models.RecipeMatchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * RecommendationEngine - Calculates recipe recommendations based on ingredient matching
 * Uses scoring algorithm to rank recipes by match percentage
 *
 * @author [Your Name]
 * @version 1.0
 */
public class RecommendationEngine {

    private static RecommendationEngine instance;
    private final RecipeManager recipeManager;

    private RecommendationEngine() {
        this.recipeManager = RecipeManager.getInstance();
    }

    /**
     * Get singleton instance
     *
     * @return RecommendationEngine instance
     */
    public static RecommendationEngine getInstance() {
        if (instance == null) {
            instance = new RecommendationEngine();
        }
        return instance;
    }

    /**
     * Get recipe recommendations based on available ingredients
     *
     * @param availableIngredients User's available ingredients
     * @return List of recommendations sorted by match score
     */
    public List<RecipeMatchResult> getRecommendations(List<Ingredient> availableIngredients) {
        try {
            List<Recipe> allRecipes = recipeManager.getAllRecipes();
            List<RecipeMatchResult> results = new ArrayList<>();

            for (Recipe recipe : allRecipes) {
                RecipeMatchResult matchResult = calculateMatch(recipe, availableIngredients);

                // Include only recipes with at least 1 matching ingredient
                if (matchResult.getMatchedIngredientsCount() > 0) {
                    results.add(matchResult);
                }
            }

            // Sort by match score (highest first)
            Collections.sort(results);

            return results;

        } catch (Exception e) {
            System.err.println("Error generating recommendations: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Calculate match score for a recipe
     *
     * @param recipe               Recipe to check
     * @param availableIngredients User's available ingredients
     * @return Match result with score and details
     */
    private RecipeMatchResult calculateMatch(Recipe recipe, List<Ingredient> availableIngredients) {
        List<Ingredient> matchedIngredients = new ArrayList<>();
        List<Ingredient> missingIngredients = new ArrayList<>();

        for (Ingredient recipeIngredient : recipe.getIngredients()) {
            boolean found = false;

            for (Ingredient availableIngredient : availableIngredients) {
                if (recipeIngredient.matches(availableIngredient)) {
                    matchedIngredients.add(recipeIngredient);
                    found = true;
                    break;
                }
            }

            if (!found) {
                missingIngredients.add(recipeIngredient);
            }
        }

        return new RecipeMatchResult(recipe, matchedIngredients, missingIngredients);
    }

    /**
     * Get top N recommendations
     *
     * @param availableIngredients User's available ingredients
     * @param limit                Maximum results to return
     * @return Top N recommendations
     */
    public List<RecipeMatchResult> getTopRecommendations(List<Ingredient> availableIngredients, int limit) {
        List<RecipeMatchResult> allRecommendations = getRecommendations(availableIngredients);

        if (allRecommendations.size() <= limit) {
            return allRecommendations;
        } else {
            return allRecommendations.subList(0, limit);
        }
    }

    /**
     * Get perfect match recipes (100% match)
     *
     * @param availableIngredients User's available ingredients
     * @return Recipes with all ingredients available
     */
    public List<RecipeMatchResult> getPerfectMatches(List<Ingredient> availableIngredients) {
        return getRecommendations(availableIngredients).stream()
                .filter(RecipeMatchResult::isPerfectMatch)
                .toList();
    }
}
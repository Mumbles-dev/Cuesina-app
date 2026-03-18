package com.cuesina.models;

import java.util.List;

/**
 * RecipeMatchResult - Stores the result of matching a recipe against user's ingredients
 * Used by the recommendation engine to rank recipes
 */
public class RecipeMatchResult implements Comparable<RecipeMatchResult> {

    private final Recipe recipe;
    private final int matchedIngredientsCount;
    private final int totalIngredientsCount;
    private final double matchPercentage;
    private final List<Ingredient> matchedIngredients;
    private final List<Ingredient> missingIngredients;

    public RecipeMatchResult(Recipe recipe,
                             List<Ingredient> matchedIngredients,
                             List<Ingredient> missingIngredients) {
        this.recipe = recipe;
        this.matchedIngredients = matchedIngredients;
        this.missingIngredients = missingIngredients;
        this.matchedIngredientsCount = matchedIngredients.size();
        this.totalIngredientsCount = recipe.getIngredientCount();

        // Calculate match percentage
        if (totalIngredientsCount > 0) {
            this.matchPercentage = (matchedIngredientsCount * 100.0) / totalIngredientsCount;
        } else {
            this.matchPercentage = 0;
        }
    }

    // Getters
    public Recipe getRecipe() {
        return recipe;
    }

    public int getMatchedIngredientsCount() {
        return matchedIngredientsCount;
    }

    public int getTotalIngredientsCount() {
        return totalIngredientsCount;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public List<Ingredient> getMatchedIngredients() {
        return matchedIngredients;
    }

    public List<Ingredient> getMissingIngredients() {
        return missingIngredients;
    }

    /**
     * Check if this is a perfect match (all ingredients available)
     */
    public boolean isPerfectMatch() {
        return matchedIngredientsCount == totalIngredientsCount;
    }

    /**
     * Get match score for ranking (higher is better)
     * Priority: matched count first, then percentage
     */
    public int getMatchScore() {
        return matchedIngredientsCount * 100 + (int) matchPercentage;
    }

    /**
     * Compare for sorting (highest match score first)
     */
    @Override
    public int compareTo(RecipeMatchResult other) {
        return Integer.compare(other.getMatchScore(), this.getMatchScore());
    }

    @Override
    public String toString() {
        return String.format("%s: %.1f%% match (%d/%d ingredients)",
                recipe.getName(), matchPercentage,
                matchedIngredientsCount, totalIngredientsCount);
    }
}
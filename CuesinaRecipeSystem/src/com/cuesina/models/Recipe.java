package com.cuesina.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe - Represents a cooking recipe with ingredients and procedure
 *
 * @author [Your Name]
 * @version 1.0
 */
public class Recipe {

    private int id;
    private String name;
    private String procedure;
    private List<Ingredient> ingredients;
    private LocalDateTime createdAt;

    /**
     * Constructor for new recipe
     *
     * @param name      Recipe name
     * @param procedure Cooking instructions
     */
    public Recipe(String name, String procedure) {
        this.name = name;
        this.procedure = procedure;
        this.ingredients = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructor for loading recipe from database
     *
     * @param id        Recipe ID
     * @param name      Recipe name
     * @param procedure Cooking instructions
     * @param createdAt Creation timestamp
     */
    public Recipe(int id, String name, String procedure, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.procedure = procedure;
        this.ingredients = new ArrayList<>();
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Add ingredient to recipe
     *
     * @param ingredient Ingredient to add
     */
    public void addIngredient(Ingredient ingredient) {
        if (!ingredients.contains(ingredient)) {
            ingredients.add(ingredient);
        }
    }

    /**
     * Remove ingredient from recipe
     *
     * @param ingredient Ingredient to remove
     */
    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
    }

    /**
     * Get ingredient count
     *
     * @return Number of ingredients
     */
    public int getIngredientCount() {
        return ingredients.size();
    }

    /**
     * Check if recipe contains specific ingredient
     *
     * @param ingredientName Name of ingredient
     * @return true if ingredient exists
     */
    public boolean hasIngredient(String ingredientName) {
        return ingredients.stream()
                .anyMatch(ing -> ing.matches(ingredientName));
    }

    /**
     * Get ingredients by category
     *
     * @param category Category to filter by
     * @return List of ingredients in category
     */
    public List<Ingredient> getIngredientsByCategory(String category) {
        return ingredients.stream()
                .filter(ing -> ing.getCategory().equals(category))
                .toList();
    }

    @Override
    public String toString() {
        return name + " (" + ingredients.size() + " ingredients)";
    }
}
package com.cuesina.models;

/**
 * Ingredient - Represents a single recipe ingredient with category
 *
 * @author [Your Name]
 * @version 1.0
 */
public class Ingredient {

    // Ingredient categories
    public static final String CATEGORY_MEAT = "Meat";
    public static final String CATEGORY_VEGGIES = "Veggies";
    public static final String CATEGORY_SEASONING = "Seasoning";
    public static final String CATEGORY_DAIRY = "Dairy";
    public static final String CATEGORY_GRAIN = "Grain";
    public static final String CATEGORY_OTHER = "Other";
    private int id;
    private String name;
    private String category;

    /**
     * Constructor for new ingredient
     *
     * @param name     Ingredient name
     * @param category Ingredient category
     */
    public Ingredient(String name, String category) {
        this.name = name;
        this.category = category;
    }

    /**
     * Constructor for loading from database
     *
     * @param id       Ingredient ID
     * @param name     Ingredient name
     * @param category Ingredient category
     */
    public Ingredient(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Check if this ingredient matches another (case-insensitive)
     *
     * @param other Ingredient to compare
     * @return true if names match
     */
    public boolean matches(Ingredient other) {
        return this.name.trim().equalsIgnoreCase(other.name.trim());
    }

    /**
     * Check if this ingredient matches a name string (case-insensitive)
     *
     * @param ingredientName Name to compare
     * @return true if names match
     */
    public boolean matches(String ingredientName) {
        return this.name.trim().equalsIgnoreCase(ingredientName.trim());
    }

    @Override
    public String toString() {
        return name + " (" + category + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ingredient that = (Ingredient) obj;
        return name.equalsIgnoreCase(that.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}
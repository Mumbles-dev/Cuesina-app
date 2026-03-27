package com.cuesina.services;

import com.cuesina.database.DatabaseManager;
import com.cuesina.models.Ingredient;
import com.cuesina.models.Recipe;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * RecipeManager - Service for managing recipe CRUD operations
 * Implements Singleton pattern for consistent data access
 *
 * @author [Your Name]
 * @version 1.0
 */
public class RecipeManager {

    private static RecipeManager instance;
    private final DatabaseManager dbManager;

    private RecipeManager() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Get singleton instance
     *
     * @return RecipeManager instance
     */
    public static RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }

    /**
     * Save new recipe to database
     *
     * @param recipe Recipe to save
     * @return Recipe ID
     * @throws SQLException if save fails
     */
    public int saveRecipe(Recipe recipe) throws SQLException {
        String sql = "INSERT INTO recipes (name, procedure) VALUES (?, ?)";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getProcedure());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int recipeId = rs.getInt(1);
                recipe.setId(recipeId);
                saveRecipeIngredients(recipeId, recipe.getIngredients());
                return recipeId;
            }
        }
        return -1;
    }

    /**
     * Update existing recipe
     *
     * @param recipe Recipe to update
     * @throws SQLException if update fails
     */
    public void updateRecipe(Recipe recipe) throws SQLException {
        String sql = "UPDATE recipes SET name = ?, procedure = ? WHERE id = ?";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, recipe.getName());
            pstmt.setString(2, recipe.getProcedure());
            pstmt.setInt(3, recipe.getId());
            pstmt.executeUpdate();

            deleteRecipeIngredients(recipe.getId());
            saveRecipeIngredients(recipe.getId(), recipe.getIngredients());
        }
    }

    /**
     * Delete recipe by ID
     *
     * @param recipeId Recipe ID to delete
     * @throws SQLException if deletion fails
     */
    public void deleteRecipe(int recipeId) throws SQLException {
        deleteRecipeIngredients(recipeId);

        String sql = "DELETE FROM recipes WHERE id = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Get recipe by ID
     *
     * @param id Recipe ID
     * @return Recipe object or null
     * @throws SQLException if query fails
     */
    public Recipe getRecipeById(int id) throws SQLException {
        String sql = "SELECT * FROM recipes WHERE id = ?";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Recipe recipe = new Recipe(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("procedure"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                recipe.setIngredients(loadRecipeIngredients(id));
                return recipe;
            }
        }
        return null;
    }

    /**
     * Get all recipes
     *
     * @return List of all recipes
     * @throws SQLException if query fails
     */
    public List<Recipe> getAllRecipes() throws SQLException {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM recipes ORDER BY created_at DESC";

        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Recipe recipe = new Recipe(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("procedure"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                recipe.setIngredients(loadRecipeIngredients(recipe.getId()));
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    /**
     * Search recipes by name
     *
     * @param searchTerm Search term
     * @return List of matching recipes
     * @throws SQLException if query fails
     */
    public List<Recipe> searchRecipesByName(String searchTerm) throws SQLException {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM recipes WHERE name LIKE ? ORDER BY name";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Recipe recipe = new Recipe(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("procedure"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                recipe.setIngredients(loadRecipeIngredients(recipe.getId()));
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    // ========== INGREDIENT MANAGEMENT ==========

    /**
     * Save recipe ingredients
     */
    private void saveRecipeIngredients(int recipeId, List<Ingredient> ingredients) throws SQLException {
        for (Ingredient ingredient : ingredients) {
            int ingredientId = getOrCreateIngredient(ingredient);
            linkIngredientToRecipe(recipeId, ingredientId);
        }
    }

    /**
     * Get or create ingredient
     *
     * @return Ingredient ID
     */
    private int getOrCreateIngredient(Ingredient ingredient) throws SQLException {
        String selectSql = "SELECT id FROM ingredients WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(selectSql)) {
            pstmt.setString(1, ingredient.getName());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        String insertSql = "INSERT INTO ingredients (name, category) VALUES (?, ?)";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(insertSql,
                Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ingredient.getName());
            pstmt.setString(2, ingredient.getCategory());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        throw new SQLException("Failed to create ingredient: " + ingredient.getName());
    }

    /**
     * Link ingredient to recipe
     */
    private void linkIngredientToRecipe(int recipeId, int ingredientId) throws SQLException {
        String sql = "INSERT OR IGNORE INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            pstmt.setInt(2, ingredientId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Delete all ingredients for recipe
     */
    private void deleteRecipeIngredients(int recipeId) throws SQLException {
        String sql = "DELETE FROM recipe_ingredients WHERE recipe_id = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Load ingredients for recipe
     */
    private List<Ingredient> loadRecipeIngredients(int recipeId) throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();

        String sql = """
                SELECT i.id, i.name, i.category
                FROM ingredients i
                INNER JOIN recipe_ingredients ri ON i.id = ri.ingredient_id
                WHERE ri.recipe_id = ?
                ORDER BY i.category, i.name
                """;

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, recipeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category")
                );
                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }

    /**
     * Get all unique ingredients
     *
     * @return List of all ingredients
     * @throws SQLException if query fails
     */
    public List<Ingredient> getAllIngredients() throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM ingredients ORDER BY category, name";

        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category")
                );
                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }

    /**
     * Get ingredients by category
     *
     * @param category Category to filter
     * @return List of ingredients in category
     * @throws SQLException if query fails
     */
    public List<Ingredient> getIngredientsByCategory(String category) throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM ingredients WHERE category = ? ORDER BY name";

        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category")
                );
                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }
}
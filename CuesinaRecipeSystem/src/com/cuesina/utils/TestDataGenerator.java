package com.cuesina.utils;

import com.cuesina.models.Ingredient;
import com.cuesina.models.Recipe;
import com.cuesina.services.RecipeManager;

import java.sql.SQLException;

/**
 * TestDataGenerator - Creates sample recipes for testing
 * Useful for development and demonstration
 */
public class TestDataGenerator {

    private final RecipeManager recipeManager;

    public TestDataGenerator() {
        this.recipeManager = RecipeManager.getInstance();
    }

    /**
     * Add sample recipes to the database
     */
    public void generateSampleData() {
        try {
            System.out.println("\n=== Generating Sample Data ===");

            // Recipe 1: Adobo (Filipino dish)
            Recipe adobo = new Recipe("Adobo",
                    "1. Marinate chicken in soy sauce and vinegar for 30 minutes.\n" +
                            "2. Heat oil in a pan and sauté garlic until golden.\n" +
                            "3. Add marinated chicken and cook until browned.\n" +
                            "4. Add water, bay leaves, and peppercorns.\n" +
                            "5. Simmer for 30-40 minutes until chicken is tender.\n" +
                            "6. Season with salt and pepper to taste.\n" +
                            "7. Serve hot with rice.");

            adobo.addIngredient(new Ingredient("Chicken", Ingredient.CATEGORY_MEAT));
            adobo.addIngredient(new Ingredient("Soy Sauce", Ingredient.CATEGORY_SEASONING));
            adobo.addIngredient(new Ingredient("Vinegar", Ingredient.CATEGORY_SEASONING));
            adobo.addIngredient(new Ingredient("Garlic", Ingredient.CATEGORY_SEASONING));
            adobo.addIngredient(new Ingredient("Bay Leaves", Ingredient.CATEGORY_SEASONING));
            adobo.addIngredient(new Ingredient("Peppercorns", Ingredient.CATEGORY_SEASONING));

            recipeManager.saveRecipe(adobo);
            System.out.println("✓ Added recipe: " + adobo.getName());

            // Recipe 2: Chicken Curry
            Recipe curry = new Recipe("Chicken Curry",
                    "1. Cut chicken into cubes.\n" +
                            "2. Sauté onions and garlic in oil until soft.\n" +
                            "3. Add curry powder and cook for 1 minute.\n" +
                            "4. Add chicken and cook until browned.\n" +
                            "5. Add coconut milk and potatoes.\n" +
                            "6. Simmer for 20 minutes until potatoes are tender.\n" +
                            "7. Season with salt and serve with rice.");

            curry.addIngredient(new Ingredient("Chicken", Ingredient.CATEGORY_MEAT));
            curry.addIngredient(new Ingredient("Curry Powder", Ingredient.CATEGORY_SEASONING));
            curry.addIngredient(new Ingredient("Coconut Milk", Ingredient.CATEGORY_DAIRY));
            curry.addIngredient(new Ingredient("Potato", Ingredient.CATEGORY_VEGGIES));
            curry.addIngredient(new Ingredient("Onion", Ingredient.CATEGORY_VEGGIES));
            curry.addIngredient(new Ingredient("Garlic", Ingredient.CATEGORY_SEASONING));

            recipeManager.saveRecipe(curry);
            System.out.println("✓ Added recipe: " + curry.getName());

            // Recipe 3: Vegetable Stir Fry
            Recipe stirFry = new Recipe("Vegetable Stir Fry",
                    "1. Heat oil in a wok over high heat.\n" +
                            "2. Add garlic and ginger, stir fry for 30 seconds.\n" +
                            "3. Add carrots and broccoli, stir fry for 3 minutes.\n" +
                            "4. Add bell peppers and mushrooms.\n" +
                            "5. Add soy sauce and oyster sauce.\n" +
                            "6. Stir fry for another 2 minutes.\n" +
                            "7. Serve immediately.");

            stirFry.addIngredient(new Ingredient("Carrot", Ingredient.CATEGORY_VEGGIES));
            stirFry.addIngredient(new Ingredient("Broccoli", Ingredient.CATEGORY_VEGGIES));
            stirFry.addIngredient(new Ingredient("Bell Pepper", Ingredient.CATEGORY_VEGGIES));
            stirFry.addIngredient(new Ingredient("Mushroom", Ingredient.CATEGORY_VEGGIES));
            stirFry.addIngredient(new Ingredient("Garlic", Ingredient.CATEGORY_SEASONING));
            stirFry.addIngredient(new Ingredient("Ginger", Ingredient.CATEGORY_SEASONING));
            stirFry.addIngredient(new Ingredient("Soy Sauce", Ingredient.CATEGORY_SEASONING));

            recipeManager.saveRecipe(stirFry);
            System.out.println("✓ Added recipe: " + stirFry.getName());

            // Recipe 4: Sinigang (Sour Soup)
            Recipe sinigang = new Recipe("Sinigang",
                    "1. Boil water in a pot.\n" +
                            "2. Add pork and onion, cook for 30 minutes.\n" +
                            "3. Add tomatoes and cook until soft.\n" +
                            "4. Add tamarind mix and fish sauce.\n" +
                            "5. Add vegetables (radish, eggplant, string beans).\n" +
                            "6. Cook until vegetables are tender.\n" +
                            "7. Add kangkong leaves last.\n" +
                            "8. Serve hot with rice.");

            sinigang.addIngredient(new Ingredient("Pork", Ingredient.CATEGORY_MEAT));
            sinigang.addIngredient(new Ingredient("Tomato", Ingredient.CATEGORY_VEGGIES));
            sinigang.addIngredient(new Ingredient("Onion", Ingredient.CATEGORY_VEGGIES));
            sinigang.addIngredient(new Ingredient("Radish", Ingredient.CATEGORY_VEGGIES));
            sinigang.addIngredient(new Ingredient("Eggplant", Ingredient.CATEGORY_VEGGIES));
            sinigang.addIngredient(new Ingredient("String Beans", Ingredient.CATEGORY_VEGGIES));
            sinigang.addIngredient(new Ingredient("Tamarind", Ingredient.CATEGORY_SEASONING));

            recipeManager.saveRecipe(sinigang);
            System.out.println("✓ Added recipe: " + sinigang.getName());

            System.out.println("=== Sample Data Generation Complete ===\n");

        } catch (SQLException e) {
            System.err.println("✗ Error generating sample data:");
            e.printStackTrace();
        }
    }

    /**
     * Display all recipes in the database
     */
    public void displayAllRecipes() {
        try {
            System.out.println("\n=== All Recipes in Database ===");
            var recipes = recipeManager.getAllRecipes();

            if (recipes.isEmpty()) {
                System.out.println("No recipes found.");
            } else {
                for (Recipe recipe : recipes) {
                    System.out.println("\n" + recipe.getName() + ":");
                    System.out.println("  Ingredients (" + recipe.getIngredientCount() + "):");
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        System.out.println("    - " + ingredient.getName() + " (" + ingredient.getCategory() + ")");
                    }
                }
            }

            System.out.println("\n=================================\n");

        } catch (SQLException e) {
            System.err.println("Error displaying recipes:");
            e.printStackTrace();
        }
    }
}
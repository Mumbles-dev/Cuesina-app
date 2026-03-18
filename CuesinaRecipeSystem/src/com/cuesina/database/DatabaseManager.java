package com.cuesina.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager - Manages SQLite database connection and schema
 * Implements Singleton pattern to ensure single database connection
 *
 * @author [Your Name]
 * @version 1.0
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:cuesina_recipes.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
    }

    /**
     * Get singleton instance of DatabaseManager
     *
     * @return DatabaseManager instance
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Initialize database connection and create tables
     *
     * @throws SQLException if database initialization fails
     */
    public void initializeDatabase() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        createTables();
    }

    /**
     * Create database tables if they don't exist
     */
    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Recipes table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS recipes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        procedure TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            // Ingredients table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS ingredients (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        category TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            // Recipe-Ingredients junction table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS recipe_ingredients (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        recipe_id INTEGER NOT NULL,
                        ingredient_id INTEGER NOT NULL,
                        FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
                        FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
                        UNIQUE(recipe_id, ingredient_id)
                    )
                    """);

            // Create indices for better performance
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_recipe_id ON recipe_ingredients(recipe_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_ingredient_id ON recipe_ingredients(ingredient_id)");
        }
    }

    /**
     * Get database connection
     *
     * @return Connection object
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}
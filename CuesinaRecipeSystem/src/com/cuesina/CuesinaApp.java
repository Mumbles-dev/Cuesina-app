package com.cuesina;

import com.cuesina.database.DatabaseManager;
import com.cuesina.services.RecipeManager;
import com.cuesina.ui.MainWindow;
import com.cuesina.utils.TestDataGenerator;

import javax.swing.*;

/**
 * Cuesina - Personal Recipe Recommendation System
 * <p>
 * Main application entry point. Initializes the database and launches the UI.
 * This offline-first desktop application allows users to store recipes locally
 * and get recommendations based on available ingredients.
 *
 * @author [Your Name]
 * @version 1.0
 * @since 2025-03-18
 */
public class CuesinaApp {

    static void main(String[] args) {
        // Set system look and feel for native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set look and feel: " + e.getMessage());
        }

        // Launch application on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize database
                DatabaseManager.getInstance().initializeDatabase();

                // Generate sample data only if database is empty
                generateSampleDataIfNeeded();

                // Launch main window
                MainWindow mainWindow = new MainWindow();
                mainWindow.setVisible(true);

            } catch (Exception e) {
                System.err.println("Error starting Cuesina: " + e.getMessage());
                e.printStackTrace();
                showStartupError(e);
                System.exit(1);
            }
        });
    }

    /**
     * Generate sample data if database is empty (for demonstration)
     */
    private static void generateSampleDataIfNeeded() {
        try {
            if (RecipeManager.getInstance().getAllRecipes().isEmpty()) {
                TestDataGenerator testData = new TestDataGenerator();
                testData.generateSampleData();
            }
        } catch (Exception e) {
            System.err.println("Note: Could not generate sample data");
        }
    }

    /**
     * Show startup error dialog
     */
    private static void showStartupError(Exception e) {
        JOptionPane.showMessageDialog(null,
                "Failed to start application:\n" + e.getMessage(),
                "Startup Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
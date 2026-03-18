package com.cuesina.ui;

import com.cuesina.models.Ingredient;
import com.cuesina.models.Recipe;
import com.cuesina.services.RecipeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AddRecipeDialog - Form for creating new recipes
 * Uses JSplitPane for the split layout (Ingredients | Procedure)
 */
public class AddRecipeDialog extends JDialog {
    // Additional fields
    private final List<IngredientRow> ingredientRows;
    private final RecipeManager recipeManager;
    private final MainWindow parentWindow;
    // GUI Components from form designer
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel bottomPanel;
    // Components we'll create in code
    private JLabel headerTitleLabel;
    private JButton closeButton;
    private JSplitPane splitPane;
    private JPanel ingredientsPanel;
    private JPanel procedurePanel;
    private JTextField dishNameField;
    private JPanel ingredientItemsPanel;
    private JButton addIngredientButton;
    private JTextArea procedureTextArea;
    private JButton saveButton;

    /**
     * Constructor
     */
    public AddRecipeDialog(MainWindow parent) {
        super(parent, "Add Recipe", true); // Modal dialog
        this.parentWindow = parent;
        this.recipeManager = RecipeManager.getInstance();
        this.ingredientRows = new ArrayList<>();

        // Set up dialog
        setContentPane(mainPanel);
        setSize(1000, 650);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Build the UI components in code
        buildHeader();
        buildContent();
        buildBottomPanel();

        // Add first ingredient row by default
        addIngredientRow();
    }

    /**
     * Build header with title and close button
     */
    private void buildHeader() {
        headerPanel.setLayout(new BorderLayout(10, 10));

        // Title label (center)
        headerTitleLabel = new JLabel("Cuesina", SwingConstants.CENTER);
        headerTitleLabel.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 28));
        headerPanel.add(headerTitleLabel, BorderLayout.CENTER);

        // Close button (right)
        closeButton = new JButton("×");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        closeButton.setPreferredSize(new Dimension(50, 50));
        closeButton.setBackground(new Color(255, 100, 100));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(e -> dispose());
        headerPanel.add(closeButton, BorderLayout.EAST);
    }

    /**
     * Build content area with JSplitPane
     */
    private void buildContent() {
        // Create left panel (ingredients - green)
        ingredientsPanel = createIngredientsPanel();

        // Create right panel (procedure - orange)
        procedurePanel = createProcedurePanel();

        // Create JSplitPane to split them
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ingredientsPanel, procedurePanel);
        splitPane.setDividerLocation(480); // Split at middle
        splitPane.setDividerSize(10);
        splitPane.setResizeWeight(0.5); // Equal resize
        splitPane.setOpaque(false);
        splitPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        contentPanel.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Create the ingredients panel (left side - green)
     */
    private JPanel createIngredientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(51, 153, 102)); // Dark green
        panel.setBorder(new LineBorder(Color.WHITE, 3, true)); // Rounded border effect

        // === HEADER ===
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("Ingredients");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(80, 80, 80));
        headerLabel.setBackground(new Color(180, 180, 180));
        headerLabel.setOpaque(true);
        headerLabel.setBorder(new EmptyBorder(5, 20, 5, 20));
        headerPanel.add(headerLabel);

        panel.add(headerPanel, BorderLayout.NORTH);

        // === DISH NAME FIELD ===
        JPanel dishNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        dishNamePanel.setOpaque(false);

        JLabel dishNameLabel = new JLabel("Dish Name");
        dishNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dishNameLabel.setForeground(Color.WHITE);

        dishNameField = new JTextField(20);
        dishNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        dishNamePanel.add(dishNameLabel);
        dishNamePanel.add(dishNameField);

        // Combine header and dish name into top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(dishNamePanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);

        // === INGREDIENT LIST (scrollable) ===
        ingredientItemsPanel = new JPanel();
        ingredientItemsPanel.setLayout(new BoxLayout(ingredientItemsPanel, BoxLayout.Y_AXIS));
        ingredientItemsPanel.setBackground(new Color(200, 200, 200));

        JScrollPane scrollPane = new JScrollPane(ingredientItemsPanel);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        // === ADD INGREDIENT BUTTON ===
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButtonPanel.setOpaque(false);
        addButtonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        addIngredientButton = new JButton("+");
        addIngredientButton.setFont(new Font("Segoe UI", Font.BOLD, 28));
        addIngredientButton.setPreferredSize(new Dimension(60, 60));
        addIngredientButton.setBackground(new Color(220, 220, 220));
        addIngredientButton.setFocusPainted(false);
        addIngredientButton.addActionListener(e -> addIngredientRow());

        addButtonPanel.add(addIngredientButton);
        panel.add(addButtonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create the procedure panel (right side - orange)
     */
    private JPanel createProcedurePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(255, 140, 0)); // Orange
        panel.setBorder(new LineBorder(Color.WHITE, 3, true)); // Rounded border effect

        // === HEADER ===
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("Procedure");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(80, 80, 80));
        headerLabel.setBackground(new Color(180, 180, 180));
        headerLabel.setOpaque(true);
        headerLabel.setBorder(new EmptyBorder(5, 20, 5, 20));
        headerPanel.add(headerLabel);

        panel.add(headerPanel, BorderLayout.NORTH);

        // === PROCEDURE TEXT AREA ===
        procedureTextArea = new JTextArea();
        procedureTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        procedureTextArea.setLineWrap(true);
        procedureTextArea.setWrapStyleWord(true);
        procedureTextArea.setMargin(new Insets(10, 10, 10, 10));

        // Placeholder text
        procedureTextArea.setText("Enter cooking instructions here...\n\nExample:\n1. First step\n2. Second step\n3. Third step");
        procedureTextArea.setForeground(Color.GRAY);

        // Clear placeholder on focus
        procedureTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (procedureTextArea.getText().startsWith("Enter cooking")) {
                    procedureTextArea.setText("");
                    procedureTextArea.setForeground(Color.BLACK);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(procedureTextArea);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Build bottom panel with save button
     */
    private void buildBottomPanel() {
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));

        saveButton = new JButton("Save Recipe");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveButton.setPreferredSize(new Dimension(200, 50));
        saveButton.setBackground(new Color(51, 153, 102));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e -> saveRecipe());

        bottomPanel.add(saveButton);
    }

    /**
     * Add a new ingredient row to the form
     */
    private void addIngredientRow() {
        IngredientRow row = new IngredientRow();
        ingredientRows.add(row);
        ingredientItemsPanel.add(row.getPanel());
        ingredientItemsPanel.revalidate();
        ingredientItemsPanel.repaint();
    }

    /**
     * Remove an ingredient row
     */
    private void removeIngredientRow(IngredientRow row) {
        ingredientRows.remove(row);
        ingredientItemsPanel.remove(row.getPanel());
        ingredientItemsPanel.revalidate();
        ingredientItemsPanel.repaint();
    }

    /**
     * Save the recipe to database
     */
    private void saveRecipe() {
        try {
            // Validate dish name
            String dishName = dishNameField.getText().trim();
            if (dishName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a dish name.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                dishNameField.requestFocus();
                return;
            }

            // Validate ingredients
            List<Ingredient> ingredients = new ArrayList<>();
            for (IngredientRow row : ingredientRows) {
                String ingredientName = row.getIngredientName();
                String category = row.getCategory();

                if (!ingredientName.isEmpty()) {
                    ingredients.add(new Ingredient(ingredientName, category));
                }
            }

            if (ingredients.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please add at least one ingredient.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate procedure
            String procedure = procedureTextArea.getText().trim();
            if (procedure.isEmpty() || procedure.startsWith("Enter cooking")) {
                JOptionPane.showMessageDialog(this,
                        "Please enter cooking instructions.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                procedureTextArea.requestFocus();
                return;
            }

            // Create recipe
            Recipe recipe = new Recipe(dishName, procedure);
            for (Ingredient ingredient : ingredients) {
                recipe.addIngredient(ingredient);
            }

            // Save to database
            int recipeId = recipeManager.saveRecipe(recipe);

            if (recipeId > 0) {
                JOptionPane.showMessageDialog(this,
                        "Recipe '" + dishName + "' saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Refresh parent window's recipe list
                parentWindow.refreshRecipeList();

                // Close dialog
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to save recipe. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            System.err.println("Error saving recipe: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error saving recipe: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Inner class representing one ingredient row
     */
    private class IngredientRow {
        private JPanel panel;
        private JTextField nameField;
        private JComboBox<String> categoryCombo;
        private JButton deleteButton;

        public IngredientRow() {
            createRow();
        }

        private void createRow() {
            // Main row panel
            panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            panel.setBackground(new Color(200, 200, 200));
            panel.setBorder(new EmptyBorder(2, 5, 2, 5));

            // Ingredient name field
            nameField = new JTextField(15);
            nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            // Category dropdown
            String[] categories = {
                    Ingredient.CATEGORY_MEAT,
                    Ingredient.CATEGORY_VEGGIES,
                    Ingredient.CATEGORY_SEASONING,
                    Ingredient.CATEGORY_DAIRY,
                    Ingredient.CATEGORY_GRAIN,
                    Ingredient.CATEGORY_OTHER
            };
            categoryCombo = new JComboBox<>(categories);
            categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            categoryCombo.setBackground(new Color(100, 100, 100));
            categoryCombo.setForeground(Color.WHITE);
            ((JLabel) categoryCombo.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

            // Delete button
            deleteButton = new JButton("×");
            deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
            deleteButton.setPreferredSize(new Dimension(40, 35));
            deleteButton.setBackground(new Color(200, 100, 100));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            deleteButton.addActionListener(e -> removeIngredientRow(this));

            // Add components to panel
            panel.add(nameField);
            panel.add(categoryCombo);
            panel.add(deleteButton);
        }

        public JPanel getPanel() {
            return panel;
        }

        public String getIngredientName() {
            return nameField.getText().trim();
        }

        public String getCategory() {
            return (String) categoryCombo.getSelectedItem();
        }
    }
}
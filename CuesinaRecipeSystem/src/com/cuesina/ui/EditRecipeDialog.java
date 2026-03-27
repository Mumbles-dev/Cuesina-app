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
 * EditRecipeDialog - Edit existing recipe
 * Pre-fills form with existing recipe data
 */
public class EditRecipeDialog extends JDialog {
    private final List<IngredientRow> ingredientRows;
    private final RecipeManager recipeManager;
    private final MainWindow parentWindow;
    private final Recipe recipe; // Recipe being edited
    // Components from GUI Form
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel bottomPanel;
    // Components we build in code
    private JTextField dishNameField;
    private JPanel ingredientItemsPanel;
    private JTextArea procedureTextArea;
    private JButton saveButton;
    private boolean hasUnsavedChanges = false;

    public EditRecipeDialog(MainWindow parent, Recipe recipe) {
        super(parent, "Edit Recipe", true);
        this.parentWindow = parent;
        this.recipeManager = RecipeManager.getInstance();
        this.recipe = recipe;
        this.ingredientRows = new ArrayList<>();

        setContentPane(mainPanel);
        setSize(1000, 650);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildHeader();
        buildContent();
        buildBottomPanel();

        // Load existing recipe data
        loadRecipeData();
    }

    /**
     * Build header
     */
    private void buildHeader() {
        JLabel titleLabel = new JLabel("Edit Recipe", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 28));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("×");
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
     * Build content with JSplitPane
     */
    private void buildContent() {
        JPanel ingredientsPanel = createIngredientsPanel();
        JPanel procedurePanel = createProcedurePanel();

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                ingredientsPanel,
                procedurePanel
        );
        splitPane.setDividerLocation(480);
        splitPane.setDividerSize(10);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        contentPanel.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Create ingredients panel (left - green)
     */
    private JPanel createIngredientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(51, 153, 102));
        panel.setBorder(new LineBorder(Color.WHITE, 3, true));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("Ingredients");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(80, 80, 80));
        headerLabel.setBackground(new Color(180, 180, 180));
        headerLabel.setOpaque(true);
        headerLabel.setBorder(new EmptyBorder(5, 20, 5, 20));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel);

        JPanel dishNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        dishNamePanel.setOpaque(false);

        JLabel dishNameLabel = new JLabel("Dish Name");
        dishNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dishNameLabel.setForeground(Color.WHITE);

        dishNameField = new JTextField(20);
        dishNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        dishNamePanel.add(dishNameLabel);
        dishNamePanel.add(dishNameField);
        topPanel.add(dishNamePanel);

        panel.add(topPanel, BorderLayout.NORTH);

        ingredientItemsPanel = new JPanel();
        ingredientItemsPanel.setLayout(new BoxLayout(ingredientItemsPanel, BoxLayout.Y_AXIS));
        ingredientItemsPanel.setBackground(new Color(200, 200, 200));

        JScrollPane scrollPane = new JScrollPane(ingredientItemsPanel);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButtonPanel.setOpaque(false);
        addButtonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton addIngredientButton = new JButton("+");
        addIngredientButton.setFont(new Font("Segoe UI", Font.BOLD, 28));
        addIngredientButton.setPreferredSize(new Dimension(60, 60));
        addIngredientButton.setBackground(new Color(220, 220, 220));
        addIngredientButton.setFocusPainted(false);
        addIngredientButton.addActionListener(e -> addIngredientRow("", Ingredient.CATEGORY_MEAT));

        addButtonPanel.add(addIngredientButton);
        panel.add(addButtonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create procedure panel (right - orange)
     */
    private JPanel createProcedurePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(255, 140, 0));
        panel.setBorder(new LineBorder(Color.WHITE, 3, true));

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

        procedureTextArea = new JTextArea();
        procedureTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        procedureTextArea.setLineWrap(true);
        procedureTextArea.setWrapStyleWord(true);
        procedureTextArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(procedureTextArea);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Build bottom panel
     */
    private void buildBottomPanel() {
        saveButton = new JButton("Update Recipe");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveButton.setPreferredSize(new Dimension(200, 50));
        saveButton.setBackground(new Color(51, 153, 102));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e -> updateRecipe());

        bottomPanel.add(saveButton);
    }

    // Track changes
    private void trackChanges() {
        dishNameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                hasUnsavedChanges = true;
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                hasUnsavedChanges = true;
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                hasUnsavedChanges = true;
            }
        });

        procedureTextArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                hasUnsavedChanges = true;
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                hasUnsavedChanges = true;
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                hasUnsavedChanges = true;
            }
        });
    }

    // Override window closing
    @Override
    protected void processWindowEvent(java.awt.event.WindowEvent e) {
        if (e.getID() == java.awt.event.WindowEvent.WINDOW_CLOSING) {
            if (hasUnsavedChanges) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "You have unsaved changes. Are you sure you want to close?",
                        "Unsaved Changes",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    super.processWindowEvent(e);
                }
            } else {
                super.processWindowEvent(e);
            }
        } else {
            super.processWindowEvent(e);
        }
    }

    // Call trackChanges in constructor after loadRecipeData()

    /**
     * Load existing recipe data into form
     */
    private void loadRecipeData() {
        // Set dish name
        dishNameField.setText(recipe.getName());

        // Set procedure
        procedureTextArea.setText(recipe.getProcedure());
        procedureTextArea.setForeground(Color.BLACK);

        // Load ingredients
        for (Ingredient ingredient : recipe.getIngredients()) {
            addIngredientRow(ingredient.getName(), ingredient.getCategory());
        }
    }

    /**
     * Add ingredient row (with optional pre-filled data)
     */
    private void addIngredientRow(String ingredientName, String category) {
        IngredientRow row = new IngredientRow(ingredientName, category);
        ingredientRows.add(row);
        ingredientItemsPanel.add(row.getPanel());
        ingredientItemsPanel.revalidate();
        ingredientItemsPanel.repaint();
    }

    /**
     * Remove ingredient row
     */
    private void removeIngredientRow(IngredientRow row) {
        ingredientRows.remove(row);
        ingredientItemsPanel.remove(row.getPanel());
        ingredientItemsPanel.revalidate();
        ingredientItemsPanel.repaint();
    }

    /**
     * Update recipe in database
     */
    private void updateRecipe() {
        try {
            String dishName = dishNameField.getText().trim();
            if (dishName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a dish name.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                dishNameField.requestFocus();
                return;
            }

            List<Ingredient> ingredients = new ArrayList<>();
            for (IngredientRow row : ingredientRows) {
                String ingredientName = row.getIngredientName();
                String category = row.getCategory();
                if (!ingredientName.isEmpty()) {
                    ingredients.add(new Ingredient(ingredientName, category));
                }
            }

            if (ingredients.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add at least one ingredient.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String procedure = procedureTextArea.getText().trim();
            if (procedure.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter cooking instructions.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                procedureTextArea.requestFocus();
                return;
            }

            // Update recipe object
            recipe.setName(dishName);
            recipe.setProcedure(procedure);
            recipe.setIngredients(ingredients);

            // Update in database
            recipeManager.updateRecipe(recipe);

            JOptionPane.showMessageDialog(this,
                    "Recipe '" + dishName + "' updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            parentWindow.refreshRecipeList();
            dispose();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ingredient row inner class (with pre-fill support)
     */
    private class IngredientRow {
        private final JPanel panel;
        private final JTextField nameField;
        private final JComboBox<String> categoryCombo;
        private final JButton deleteButton;

        public IngredientRow(String initialName, String initialCategory) {
            panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            panel.setBackground(new Color(200, 200, 200));
            panel.setBorder(new EmptyBorder(2, 5, 2, 5));

            nameField = new JTextField(15);
            nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            nameField.setText(initialName); // Pre-fill

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
            categoryCombo.setSelectedItem(initialCategory); // Pre-select

            deleteButton = new JButton("×");
            deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
            deleteButton.setPreferredSize(new Dimension(40, 35));
            deleteButton.setBackground(new Color(200, 100, 100));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            deleteButton.addActionListener(e -> removeIngredientRow(this));

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
package com.cuesina.ui;

import com.cuesina.models.Ingredient;
import com.cuesina.models.Recipe;
import com.cuesina.services.RecipeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * ViewRecipeDialog - Shows complete recipe details
 * Displays ingredients and cooking procedure
 */
public class ViewRecipeDialog extends JDialog {

    private final Recipe recipe;
    private final MainWindow parentWindow;
    private final RecipeManager recipeManager;

    public ViewRecipeDialog(MainWindow parent, Recipe recipe) {
        super(parent, recipe.getName(), true);
        this.parentWindow = parent;
        this.recipe = recipe;
        this.recipeManager = RecipeManager.getInstance(); // Add this line

        createUI();

        setSize(900, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Create the UI
     */
    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(102, 204, 102)); // Green background
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        createHeader(mainPanel);

        // Content with JSplitPane
        createContent(mainPanel);

        // Bottom buttons
        createBottomPanel(mainPanel);

        setContentPane(mainPanel);
    }

    /**
     * Create header with recipe name and close button
     */
    private void createHeader(JPanel mainPanel) {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Recipe name
        JLabel titleLabel = new JLabel(recipe.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Recipe info (ingredient count, created date)
        String infoText = String.format("%d ingredients • Added %s",
                recipe.getIngredientCount(),
                recipe.getCreatedAt().toLocalDate().toString()
        );
        JLabel infoLabel = new JLabel(infoText, SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(Color.GRAY);
        headerPanel.add(infoLabel, BorderLayout.SOUTH);

        // Close button
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        closeButton.setPreferredSize(new Dimension(50, 50));
        closeButton.setBackground(new Color(255, 100, 100));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(e -> dispose());
        headerPanel.add(closeButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Create content area with ingredients and procedure
     */
    private void createContent(JPanel mainPanel) {
        JPanel ingredientsPanel = createIngredientsPanel();
        JPanel procedurePanel = createProcedurePanel();

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                ingredientsPanel,
                procedurePanel
        );
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(10);
        splitPane.setResizeWeight(0.4);
        splitPane.setBorder(new EmptyBorder(10, 0, 10, 0));

        mainPanel.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Create ingredients panel (left - green)
     */
    private JPanel createIngredientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(51, 153, 102));
        panel.setBorder(new LineBorder(Color.WHITE, 3, true));

        // Header
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

        // Ingredients list grouped by category
        JPanel ingredientsListPanel = new JPanel();
        ingredientsListPanel.setLayout(new BoxLayout(ingredientsListPanel, BoxLayout.Y_AXIS));
        ingredientsListPanel.setBackground(new Color(200, 200, 200));
        ingredientsListPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Group ingredients by category
        Map<String, List<Ingredient>> groupedIngredients = recipe.getIngredients().stream()
                .collect(Collectors.groupingBy(Ingredient::getCategory));

        // Display by category
        for (String category : groupedIngredients.keySet()) {
            // Category header
            JLabel categoryLabel = new JLabel(category);
            categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            categoryLabel.setForeground(new Color(60, 60, 60));
            categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            ingredientsListPanel.add(categoryLabel);
            ingredientsListPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            // Ingredients in this category
            for (Ingredient ingredient : groupedIngredients.get(category)) {
                JPanel ingredientRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
                ingredientRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                ingredientRow.setBackground(new Color(220, 220, 220));
                ingredientRow.setBorder(new EmptyBorder(3, 10, 3, 10));

                JLabel nameLabel = new JLabel("• " + ingredient.getName());
                nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                ingredientRow.add(nameLabel);

                ingredientRow.setAlignmentX(Component.LEFT_ALIGNMENT);
                ingredientsListPanel.add(ingredientRow);
            }

            ingredientsListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(ingredientsListPanel);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create procedure panel (right - orange)
     */
    private JPanel createProcedurePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(255, 140, 0));
        panel.setBorder(new LineBorder(Color.WHITE, 3, true));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("Cooking Procedure");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(80, 80, 80));
        headerLabel.setBackground(new Color(180, 180, 180));
        headerLabel.setOpaque(true);
        headerLabel.setBorder(new EmptyBorder(5, 20, 5, 20));
        headerPanel.add(headerLabel);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Procedure text
        JTextArea procedureTextArea = new JTextArea(recipe.getProcedure());
        procedureTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        procedureTextArea.setLineWrap(true);
        procedureTextArea.setWrapStyleWord(true);
        procedureTextArea.setEditable(false);
        procedureTextArea.setMargin(new Insets(15, 15, 15, 15));
        procedureTextArea.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(procedureTextArea);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create bottom panel with action buttons
     */
    private void createBottomPanel(JPanel mainPanel) {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setOpaque(false);

        // Edit button (for Stage 8)
        JButton editButton = new JButton("Edit Recipe");
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editButton.setPreferredSize(new Dimension(150, 45));
        editButton.setBackground(new Color(51, 153, 102));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.addActionListener(e -> editRecipe());

        // Delete button (for Stage 8)
        JButton deleteButton = new JButton("Delete Recipe");
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteButton.setPreferredSize(new Dimension(150, 45));
        deleteButton.setBackground(new Color(200, 100, 100));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> deleteRecipe());

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeButton.setPreferredSize(new Dimension(100, 45));
        closeButton.setBackground(new Color(150, 150, 150));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(e -> dispose());

        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(closeButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Edit recipe
     */
    private void editRecipe() {
        dispose(); // Close current viewer
        EditRecipeDialog editDialog = new EditRecipeDialog(parentWindow, recipe);
        editDialog.setVisible(true);
    }

    /**
     * Delete recipe with confirmation
     */
    private void deleteRecipe() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + recipe.getName() + "'?\n" +
                        "This action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                recipeManager.deleteRecipe(recipe.getId());

                JOptionPane.showMessageDialog(this,
                        "Recipe '" + recipe.getName() + "' deleted successfully!",
                        "Deleted",
                        JOptionPane.INFORMATION_MESSAGE);

                parentWindow.refreshRecipeList();
                dispose();

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error deleting recipe: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
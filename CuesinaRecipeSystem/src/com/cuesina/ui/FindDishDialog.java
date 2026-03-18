package com.cuesina.ui;

import com.cuesina.models.Ingredient;
import com.cuesina.models.RecipeMatchResult;
import com.cuesina.services.RecommendationEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FindDishDialog - Ingredient-based recipe recommendation
 * Structure from GUI Form, dynamic content in code
 */
public class FindDishDialog extends JDialog {
    private final List<IngredientRow> ingredientRows;
    private final RecommendationEngine recommendationEngine;
    // Components from GUI Form Designer
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JPanel bottomPanel;
    // Components we build in code
    private JPanel ingredientItemsPanel;
    private JPanel recommendationsPanel;
    private JButton findButton;
    private List<RecipeMatchResult> currentRecommendations;

    public FindDishDialog(JFrame parent) {
        super(parent, "Find a Dish", true);
        this.recommendationEngine = RecommendationEngine.getInstance();
        this.ingredientRows = new ArrayList<>();
        this.currentRecommendations = new ArrayList<>();

        setContentPane(mainPanel);
        setSize(1000, 650);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildHeader();
        buildContent();
        buildBottomPanel();

        // Add first ingredient row
        addIngredientRow();
    }

    /**
     * Build header
     */
    private void buildHeader() {
        JLabel titleLabel = new JLabel("Cuesina", SwingConstants.CENTER);
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
        JPanel recommendationsContainerPanel = createRecommendationsPanel();

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                ingredientsPanel,
                recommendationsContainerPanel
        );
        splitPane.setDividerLocation(480);
        splitPane.setDividerSize(10);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        contentPanel.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Create ingredients input panel (left - green)
     */
    private JPanel createIngredientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(51, 153, 102));
        panel.setBorder(new LineBorder(Color.WHITE, 3, true));

        // Header
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

        // Info label
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setOpaque(false);
        JLabel infoLabel = new JLabel("What ingredients do you have?");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(Color.WHITE);
        infoPanel.add(infoLabel);
        topPanel.add(infoPanel);

        panel.add(topPanel, BorderLayout.NORTH);

        // Ingredient list
        ingredientItemsPanel = new JPanel();
        ingredientItemsPanel.setLayout(new BoxLayout(ingredientItemsPanel, BoxLayout.Y_AXIS));
        ingredientItemsPanel.setBackground(new Color(200, 200, 200));

        JScrollPane scrollPane = new JScrollPane(ingredientItemsPanel);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add button
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButtonPanel.setOpaque(false);
        addButtonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton addIngredientButton = new JButton("+");
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
     * Create recommendations panel (right - orange)
     */
    private JPanel createRecommendationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(255, 140, 0));
        panel.setBorder(new LineBorder(Color.WHITE, 3, true));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("Recommended Dishes");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(80, 80, 80));
        headerLabel.setBackground(new Color(180, 180, 180));
        headerLabel.setOpaque(true);
        headerLabel.setBorder(new EmptyBorder(5, 20, 5, 20));
        headerPanel.add(headerLabel);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Recommendations list area
        recommendationsPanel = new JPanel();
        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.Y_AXIS));
        recommendationsPanel.setBackground(new Color(200, 200, 200));

        // Initial message
        JLabel initialLabel = new JLabel("Click 'Find Dishes' to see recommendations", SwingConstants.CENTER);
        initialLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        initialLabel.setForeground(Color.GRAY);
        initialLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        recommendationsPanel.add(Box.createVerticalGlue());
        recommendationsPanel.add(initialLabel);
        recommendationsPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(recommendationsPanel);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Build bottom panel
     */
    private void buildBottomPanel() {
        findButton = new JButton("Find Dishes");
        findButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        findButton.setPreferredSize(new Dimension(200, 50));
        findButton.setBackground(new Color(255, 140, 0));
        findButton.setForeground(Color.WHITE);
        findButton.setFocusPainted(false);
        findButton.setBorderPainted(false);
        findButton.addActionListener(e -> findRecommendations());

        bottomPanel.add(findButton);
    }

    /**
     * Add ingredient row
     */
    private void addIngredientRow() {
        IngredientRow row = new IngredientRow();
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
     * Find recommendations based on user's ingredients
     */
    private void findRecommendations() {
        // Collect user's ingredients
        List<Ingredient> userIngredients = new ArrayList<>();
        for (IngredientRow row : ingredientRows) {
            String ingredientName = row.getIngredientName();
            String category = row.getCategory();
            if (!ingredientName.isEmpty()) {
                userIngredients.add(new Ingredient(ingredientName, category));
            }
        }

        if (userIngredients.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please add at least one ingredient.",
                    "No Ingredients",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show loading state
        findButton.setEnabled(false);
        findButton.setText("Searching...");

        // Use SwingWorker for background processing (prevents UI freeze)
        SwingWorker<List<RecipeMatchResult>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<RecipeMatchResult> doInBackground() {
                return recommendationEngine.getRecommendations(userIngredients);
            }

            @Override
            protected void done() {
                try {
                    currentRecommendations = get();
                    displayRecommendations();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(FindDishDialog.this,
                            "Error finding recommendations: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    findButton.setEnabled(true);
                    findButton.setText("Find Dishes");
                }
            }
        };

        worker.execute();
    }

    /**
     * Display recommendations in the panel
     */
    private void displayRecommendations() {
        recommendationsPanel.removeAll();

        if (currentRecommendations.isEmpty()) {
            JLabel noResultsLabel = new JLabel("No matching recipes found", SwingConstants.CENTER);
            noResultsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noResultsLabel.setForeground(Color.GRAY);
            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            recommendationsPanel.add(Box.createVerticalGlue());
            recommendationsPanel.add(noResultsLabel);
            recommendationsPanel.add(Box.createVerticalGlue());
        } else {
            for (RecipeMatchResult result : currentRecommendations) {
                JPanel recipeCard = createRecipeCard(result);
                recommendationsPanel.add(recipeCard);
                recommendationsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        recommendationsPanel.revalidate();
        recommendationsPanel.repaint();
    }

    /**
     * Create a recipe recommendation card
     */
    private JPanel createRecipeCard(RecipeMatchResult result) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setBackground(new Color(220, 220, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 180), 2, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Recipe name
        JLabel nameLabel = new JLabel(result.getRecipe().getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        card.add(nameLabel, BorderLayout.NORTH);

        // Match info
        String matchText = String.format("%.0f%% match (%d/%d ingredients)",
                result.getMatchPercentage(),
                result.getMatchedIngredientsCount(),
                result.getTotalIngredientsCount());
        JLabel matchLabel = new JLabel(matchText);
        matchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        if (result.isPerfectMatch()) {
            matchLabel.setForeground(new Color(0, 150, 0));
            matchLabel.setText("✓ PERFECT MATCH - " + matchText);
        } else {
            matchLabel.setForeground(new Color(100, 100, 100));
        }

        card.add(matchLabel, BorderLayout.CENTER);

        // Click to view details
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showRecipeDetails(result);
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(240, 240, 240));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(220, 220, 220));
            }
        });

        return card;
    }

    /**
     * Show recipe details popup
     */
    private void showRecipeDetails(RecipeMatchResult result) {
        RecipeDetailDialog detailDialog = new RecipeDetailDialog(this, result);
        detailDialog.setVisible(true);
    }

    /**
     * Ingredient row inner class
     */
    private class IngredientRow {
        private final JPanel panel;
        private final JTextField nameField;
        private final JComboBox<String> categoryCombo;
        private final JButton deleteButton;

        public IngredientRow() {
            panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            panel.setBackground(new Color(200, 200, 200));
            panel.setBorder(new EmptyBorder(2, 5, 2, 5));

            nameField = new JTextField(15);
            nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

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
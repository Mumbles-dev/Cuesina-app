package com.cuesina.ui;

import com.cuesina.models.Ingredient;
import com.cuesina.models.RecipeMatchResult;
import com.cuesina.services.RecommendationEngine;
import com.cuesina.utils.UIColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FindDishDialog - Beautiful rounded design for finding recipes
 */
public class FindDishDialog extends JDialog {

    private JPanel ingredientItemsPanel;
    private JPanel recommendationsPanel;
    private List<IngredientRow> ingredientRows;
    private List<RecipeMatchResult> currentRecommendations;
    private RecommendationEngine recommendationEngine;
    private MainWindow parentWindow;

    public FindDishDialog(MainWindow parent) {
        super(parent, true);
        this.parentWindow = parent;
        this.recommendationEngine = RecommendationEngine.getInstance();
        this.ingredientRows = new ArrayList<>();
        this.currentRecommendations = new ArrayList<>();

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        setSize(1100, 650);
        setLocationRelativeTo(parent);

        createUI();

        // Add first ingredient row
        addIngredientRow();
    }

    /**
     * Create complete UI
     */
    private void createUI() {
        // Layer 1: Orange background
        JPanel orangeLayer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIColors.ACCENT_ORANGE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.dispose();
            }
        };
        orangeLayer.setOpaque(false);
        orangeLayer.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Layer 2: White background
        JPanel whiteLayer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        whiteLayer.setOpaque(false);
        whiteLayer.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Content
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);

        contentPanel.add(createIngredientsPanel());
        contentPanel.add(createRecommendationsPanel());

        whiteLayer.add(contentPanel, BorderLayout.CENTER);
        orangeLayer.add(whiteLayer, BorderLayout.CENTER);

        // Close button
        JButton closeButton = createCloseButton();

        // Layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1100, 650));

        orangeLayer.setBounds(0, 0, 1100, 650);
        closeButton.setBounds(1020, 20, 60, 60);

        layeredPane.add(orangeLayer, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(closeButton, JLayeredPane.PALETTE_LAYER);

        setContentPane(layeredPane);
    }

    /**
     * Create close button
     */
    private JButton createCloseButton() {
        JButton closeButton = new JButton("×") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIColors.BUTTON_CLOSE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };

        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 32));
        closeButton.setForeground(Color.WHITE);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());

        return closeButton;
    }

    /**
     * Create ingredients panel
     */
    private JPanel createIngredientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0xF5DDB3));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.setColor(new Color(180, 60, 60));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);

                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JLabel headerLabel = new JLabel("Ingredients", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setOpaque(true);
        headerLabel.setBackground(UIColors.ACCENT_ORANGE);
        headerLabel.setBorder(new EmptyBorder(8, 20, 8, 20));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        headerPanel.setOpaque(false);
        headerPanel.add(headerLabel);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Center: Ingredient rows
        ingredientItemsPanel = new JPanel();
        ingredientItemsPanel.setLayout(new BoxLayout(ingredientItemsPanel, BoxLayout.Y_AXIS));
        ingredientItemsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(ingredientItemsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom: + button and Find Dishes button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        // + button (right)
        JButton addButton = createAddButton();
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        addButtonPanel.setOpaque(false);
        addButtonPanel.add(addButton);

        // Find Dishes button (left)
        JButton findButton = createFindButton();
        JPanel findButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        findButtonPanel.setOpaque(false);
        findButtonPanel.add(findButton);

        bottomPanel.add(findButtonPanel, BorderLayout.WEST);
        bottomPanel.add(addButtonPanel, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create recommendations panel
     */
    private JPanel createRecommendationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0xF5DDB3));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.setColor(new Color(180, 60, 60));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);

                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JLabel headerLabel = new JLabel("Recommended Dishes", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setOpaque(true);
        headerLabel.setBackground(UIColors.ACCENT_ORANGE);
        headerLabel.setBorder(new EmptyBorder(8, 20, 8, 20));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        headerPanel.setOpaque(false);
        headerPanel.add(headerLabel);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Recommendations list
        recommendationsPanel = new JPanel();
        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.Y_AXIS));
        recommendationsPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(recommendationsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create green + button
     */
    private JButton createAddButton() {
        JButton button = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(100, 180, 80));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 36));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(60, 60));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> addIngredientRow());

        return button;
    }

    /**
     * Create Find Dishes button
     */
    private JButton createFindButton() {
        JButton button = new JButton("Find Dishes") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color btnColor = new Color(100, 180, 80);
                if (getModel().isPressed()) {
                    g2.setColor(btnColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(btnColor.brighter());
                } else {
                    g2.setColor(btnColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(160, 50));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> findRecommendations());

        return button;
    }

    /**
     * Add ingredient row
     */
    private void addIngredientRow() {
        IngredientRow row = new IngredientRow();
        ingredientRows.add(row);
        ingredientItemsPanel.add(row.getPanel());
        ingredientItemsPanel.add(Box.createVerticalStrut(10));
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
     * Find recommendations
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

        // Get recommendations
        currentRecommendations = recommendationEngine.getRecommendations(userIngredients);

        // Display recommendations
        displayRecommendations();
    }

    /**
     * Display recommendations
     */
    private void displayRecommendations() {
        recommendationsPanel.removeAll();

        if (currentRecommendations.isEmpty()) {
            JLabel noResultsLabel = new JLabel("No matching recipes found", SwingConstants.CENTER);
            noResultsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noResultsLabel.setForeground(Color.GRAY);
            recommendationsPanel.add(noResultsLabel);
        } else {
            for (RecipeMatchResult result : currentRecommendations) {
                JPanel recipeCard = createRecipeCard(result);
                recommendationsPanel.add(recipeCard);
                recommendationsPanel.add(Box.createVerticalStrut(10));
            }
        }

        recommendationsPanel.revalidate();
        recommendationsPanel.repaint();
    }

    /**
     * Create recipe recommendation card
     */
    private JPanel createRecipeCard(RecipeMatchResult result) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Rounded white background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Border
                g2.setColor(new Color(120, 120, 120));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Recipe name
        JLabel nameLabel = new JLabel(result.getRecipe().getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Match percentage
        JLabel matchLabel = new JLabel(String.format("%.0f%% match", result.getMatchPercentage()));
        matchLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        matchLabel.setForeground(result.isPerfectMatch() ? new Color(0, 150, 0) : UIColors.ACCENT_ORANGE);

        // Perfect match indicator
        if (result.isPerfectMatch()) {
            JLabel perfectLabel = new JLabel("✓ Perfect Match!");
            perfectLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            perfectLabel.setForeground(new Color(0, 150, 0));

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setOpaque(false);
            topPanel.add(nameLabel, BorderLayout.CENTER);
            topPanel.add(perfectLabel, BorderLayout.SOUTH);

            card.add(topPanel, BorderLayout.CENTER);
        } else {
            card.add(nameLabel, BorderLayout.CENTER);
        }

        card.add(matchLabel, BorderLayout.EAST);

        // Click to view details
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RecipeDetailDialog detailDialog = new RecipeDetailDialog(FindDishDialog.this, result, parentWindow, FindDishDialog.this);
                detailDialog.setVisible(true);
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(240, 240, 240));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    /**
     * Ingredient row with rounded box and pill-shaped dropdown
     */
    private class IngredientRow {
        private JPanel panel;
        private JTextField nameField;
        private JButton categoryButton;
        private JButton deleteButton;
        private String selectedCategory;

        public IngredientRow() {
            selectedCategory = Ingredient.CATEGORY_MEAT;

            // Rounded wrapper panel
            panel = new JPanel(new BorderLayout(10, 0)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Rounded white background
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                    // Border
                    g2.setColor(new Color(120, 120, 120));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 40, 40);

                    g2.dispose();
                }
            };
            panel.setOpaque(false);
            panel.setBorder(new EmptyBorder(12, 20, 12, 15));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

            // Name field (bigger font)
            nameField = new JTextField();
            nameField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            nameField.setBorder(null);
            nameField.setOpaque(false);

            // Category button (pill-shaped dropdown trigger)
            categoryButton = new JButton("Meat ▼");
            categoryButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            categoryButton.setBackground(UIColors.BUTTON_RED);
            categoryButton.setForeground(Color.WHITE);
            categoryButton.setPreferredSize(new Dimension(120, 40));
            categoryButton.setFocusPainted(false);
            categoryButton.setBorderPainted(false);
            categoryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            categoryButton.addActionListener(e -> showCategoryPopup());

            // Delete button
            deleteButton = new JButton("×");
            deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
            deleteButton.setPreferredSize(new Dimension(40, 40));
            deleteButton.setBackground(new Color(200, 100, 100));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setFocusPainted(false);
            deleteButton.setBorderPainted(false);
            deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteButton.addActionListener(e -> removeIngredientRow(this));

            // Right panel
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            rightPanel.setOpaque(false);
            rightPanel.add(categoryButton);
            rightPanel.add(deleteButton);

            panel.add(nameField, BorderLayout.CENTER);
            panel.add(rightPanel, BorderLayout.EAST);
        }

        /**
         * Show custom category popup
         */
        private void showCategoryPopup() {
            JPopupMenu popup = new JPopupMenu();
            popup.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            popup.setBackground(Color.WHITE);

            String[] categories = {
                    Ingredient.CATEGORY_MEAT,
                    Ingredient.CATEGORY_VEGGIES,
                    "ETC"
            };

            for (String category : categories) {
                JButton pillButton = new JButton(category) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(UIColors.BUTTON_RED);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };

                pillButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
                pillButton.setForeground(Color.WHITE);
                pillButton.setPreferredSize(new Dimension(260, 50));
                pillButton.setMaximumSize(new Dimension(260, 50));
                pillButton.setContentAreaFilled(false);
                pillButton.setBorderPainted(false);
                pillButton.setFocusPainted(false);
                pillButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

                String actualCategory = category.equals("ETC") ? Ingredient.CATEGORY_SEASONING : category;

                pillButton.addActionListener(e -> {
                    categoryButton.setText(category + " ▼");
                    selectedCategory = actualCategory;
                    popup.setVisible(false);
                });

                popup.add(pillButton);
                if (!category.equals(categories[categories.length - 1])) {
                    popup.add(Box.createVerticalStrut(10));
                }
            }

            popup.show(categoryButton, 0, categoryButton.getHeight() + 5);
        }

        public JPanel getPanel() { return panel; }
        public String getIngredientName() { return nameField.getText().trim(); }
        public String getCategory() { return selectedCategory; }
    }
}
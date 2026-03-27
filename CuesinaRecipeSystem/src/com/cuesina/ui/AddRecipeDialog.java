package com.cuesina.ui;

import com.cuesina.models.Ingredient;
import com.cuesina.models.Recipe;
import com.cuesina.services.RecipeManager;
import com.cuesina.utils.UIColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AddRecipeDialog - Beautiful rounded design with pill-shaped dropdown
 */
public class AddRecipeDialog extends JDialog {

    private JPanel ingredientItemsPanel;
    private JTextArea procedureTextArea;
    private List<IngredientRow> ingredientRows;
    private RecipeManager recipeManager;
    private MainWindow parentWindow;

    public AddRecipeDialog(MainWindow parent) {
        super(parent, true);
        this.parentWindow = parent;
        this.recipeManager = RecipeManager.getInstance();
        this.ingredientRows = new ArrayList<>();

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
        contentPanel.add(createProcedurePanel());

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

        // Bottom: + button and Save Recipe button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        // + button (right)
        JButton addButton = createAddButton();
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        addButtonPanel.setOpaque(false);
        addButtonPanel.add(addButton);

        // Save Recipe button (left)
        JButton saveButton = createSaveButton();
        JPanel saveButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        saveButtonPanel.setOpaque(false);
        saveButtonPanel.add(saveButton);

        bottomPanel.add(saveButtonPanel, BorderLayout.WEST);
        bottomPanel.add(addButtonPanel, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create procedure panel
     */
    private JPanel createProcedurePanel() {
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
        JLabel headerLabel = new JLabel("Procedure", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setOpaque(true);
        headerLabel.setBackground(UIColors.ACCENT_ORANGE);
        headerLabel.setBorder(new EmptyBorder(8, 20, 8, 20));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        headerPanel.setOpaque(false);
        headerPanel.add(headerLabel);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Text area
        procedureTextArea = new JTextArea();
        procedureTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        procedureTextArea.setLineWrap(true);
        procedureTextArea.setWrapStyleWord(true);
        procedureTextArea.setMargin(new Insets(10, 10, 10, 10));
        procedureTextArea.setForeground(Color.BLACK);
        procedureTextArea.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(procedureTextArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

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
     * Create save button
     */
    private JButton createSaveButton() {
        JButton button = new JButton("Save Recipe") {
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
        button.addActionListener(e -> saveRecipe());

        return button;
    }

    /**
     * Add ingredient row
     */
    private void addIngredientRow() {
        IngredientRow row = new IngredientRow();
        ingredientRows.add(row);
        ingredientItemsPanel.add(row.getPanel());
        ingredientItemsPanel.add(Box.createVerticalStrut(10)); // Spacing between rows
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
     * Save recipe
     */
    private void saveRecipe() {
        try {
            // Prompt for dish name
            String dishName = JOptionPane.showInputDialog(this,
                    "Enter dish name:",
                    "Dish Name",
                    JOptionPane.QUESTION_MESSAGE);

            if (dishName == null || dishName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Dish name is required.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dishName = dishName.trim();

            // Collect ingredients from all rows
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
                return;
            }

            Recipe recipe = new Recipe(dishName, procedure);
            recipe.setIngredients(ingredients);

            recipeManager.saveRecipe(recipe);

            JOptionPane.showMessageDialog(this,
                    "Recipe '" + dishName + "' saved successfully!",
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

            // Right panel with category and delete
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            rightPanel.setOpaque(false);
            rightPanel.add(categoryButton);
            rightPanel.add(deleteButton);

            panel.add(nameField, BorderLayout.CENTER);
            panel.add(rightPanel, BorderLayout.EAST);
        }

        /**
         * Show custom category popup with pill-shaped buttons
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

                        // Pill-shaped background
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

                // Map ETC to Seasoning
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
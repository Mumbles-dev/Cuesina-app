package com.cuesina.ui;

import com.cuesina.models.Ingredient;
import com.cuesina.models.RecipeMatchResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * RecipeDetailDialog - Shows recipe details with ingredient matching
 * Displays current ingredients vs missing ingredients
 */
public class RecipeDetailDialog extends JDialog {

    private final RecipeMatchResult matchResult;

    public RecipeDetailDialog(JDialog parent, RecipeMatchResult matchResult) {
        super(parent, "Recipe Details", true);
        this.matchResult = matchResult;

        createUI();

        setSize(700, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Recipe name header
        JLabel titleLabel = new JLabel(matchResult.getRecipe().getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Content panel with two columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(Color.WHITE);

        // Left: Current Ingredients
        JPanel currentPanel = createIngredientListPanel(
                "Current Ingredients",
                matchResult.getMatchedIngredients(),
                new Color(0, 150, 0) // Green
        );
        contentPanel.add(currentPanel);

        // Right: Missing Ingredients
        JPanel missingPanel = createIngredientListPanel(
                "Missing Ingredients",
                matchResult.getMissingIngredients(),
                new Color(200, 100, 100) // Red
        );
        contentPanel.add(missingPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Note panel
        JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        notePanel.setBackground(Color.WHITE);
        notePanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel noteLabel = new JLabel("<html><b>Note:</b> In the current ingredients, it will be color coded depending on the ingredients. " +
                "If color <font color='green'><b>Green</b></font> means right ingredient, " +
                "color <font color='orange'><b>Orange</b></font> means possible alternative/same type of ingredient, " +
                "color <font color='red'><b>Red</b></font> means not necessary ingredient</html>");
        noteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        notePanel.add(noteLabel);

        mainPanel.add(notePanel, BorderLayout.SOUTH);

        // Close button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);

        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        closeButton.setPreferredSize(new Dimension(40, 40));
        closeButton.setBackground(new Color(255, 100, 100));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(e -> dispose());

        // Position close button in top right
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        closeButton.setBounds(getWidth() - 60, 10, 40, 40);
        layeredPane.add(closeButton, JLayeredPane.PALETTE_LAYER);

        setContentPane(mainPanel);
    }

    private JPanel createIngredientListPanel(String title, java.util.List<Ingredient> ingredients, Color titleColor) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // Title
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(titleColor);
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Ingredients list
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(new EmptyBorder(5, 15, 10, 15));

        if (ingredients.isEmpty()) {
            JLabel emptyLabel = new JLabel("None");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            emptyLabel.setForeground(Color.GRAY);
            listPanel.add(emptyLabel);
        } else {
            for (Ingredient ingredient : ingredients) {
                JLabel ingredientLabel = new JLabel("• " + ingredient.getName());
                ingredientLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                // Color coding based on match (you can enhance this later)
                ingredientLabel.setForeground(titleColor);
                listPanel.add(ingredientLabel);
                listPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
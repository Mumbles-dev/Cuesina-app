package com.cuesina.ui;

import com.cuesina.models.Ingredient;
import com.cuesina.models.Recipe;
import com.cuesina.services.RecipeManager;
import com.cuesina.utils.UIColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {

    private JPanel mainPanel;
    private JPanel centerPanel; // kept as field so showRecipeInCenter can replace it
    private DefaultListModel<String> recipeListModel;
    private RecipeManager recipeManager;
    private JList<String> recipeList;

    public MainWindow() {
        recipeManager = RecipeManager.getInstance();

        setTitle("Cuesina - Personal Recipe Recommendation System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
        setupKeyboardShortcuts();
        loadRecipes();
    }

    private void createUI() {
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UIColors.WARM_ORANGE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        mainPanel.add(createHeader(), BorderLayout.NORTH);

        centerPanel = createDefaultCenterPanel(); // save reference
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        mainPanel.add(createSidebar(), BorderLayout.EAST);

        setContentPane(mainPanel);
    }

    // -------------------------------------------------------------------------
    // Header — unchanged from your original
    // -------------------------------------------------------------------------

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIColors.HEADER_BEIGE);
        headerPanel.setBorder(new LineBorder(UIColors.BORDER_ORANGE, 3, true));
        headerPanel.setPreferredSize(new Dimension(0, 100));

        JPanel logoContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoContainer.setOpaque(false);

        JLabel logoLabel = createLogoLabel();
        if (logoLabel != null) {
            logoContainer.add(logoLabel);
        } else {
            JLabel titleLabel = new JLabel("Cuesina");
            titleLabel.setFont(new Font("Brush Script MT", Font.ITALIC, 48));
            titleLabel.setForeground(UIColors.TEXT_DARK);
            logoContainer.add(titleLabel);
            System.err.println("Logo not found - using text fallback");
        }

        headerPanel.add(logoContainer, BorderLayout.CENTER);
        return headerPanel;
    }

    private JLabel createLogoLabel() {
        String[] possiblePaths = {
                "/cuesina_logo.png",
                "/com/cuesina/resources/cuesina_logo.png",
                "cuesina_logo.png",
                "src/resources/cuesina_logo.png",
                "resources/cuesina_logo.png"
        };

        for (String path : possiblePaths) {
            try {
                java.net.URL logoUrl = getClass().getResource(path);
                if (logoUrl != null) {
                    ImageIcon originalIcon = new ImageIcon(logoUrl);
                    if (originalIcon.getIconWidth() > 0) {
                        int logoHeight = 70;
                        int logoWidth  = (int) ((double) logoHeight / originalIcon.getIconHeight() * originalIcon.getIconWidth());
                        Image scaled   = originalIcon.getImage().getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
                        Image rotated  = rotateImage(scaled, -90);
                        System.out.println("✓ Logo loaded and rotated from: " + path);
                        return new JLabel(new ImageIcon(rotated));
                    }
                }
                java.io.File logoFile = new java.io.File(path);
                if (logoFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(path);
                    if (originalIcon.getIconWidth() > 0) {
                        int logoHeight = 70;
                        int logoWidth  = (int) ((double) logoHeight / originalIcon.getIconHeight() * originalIcon.getIconWidth());
                        Image scaled   = originalIcon.getImage().getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
                        Image rotated  = rotateImage(scaled, -90);
                        System.out.println("✓ Logo loaded and rotated from file: " + path);
                        return new JLabel(new ImageIcon(rotated));
                    }
                }
            } catch (Exception e) { /* try next */ }
        }
        System.err.println("✗ Logo not found in any paths");
        return null;
    }

    private Image rotateImage(Image img, double degrees) {
        ImageIcon icon    = new ImageIcon(img);
        int width         = icon.getIconWidth();
        int height        = icon.getIconHeight();
        int newWidth      = height;
        int newHeight     = width;

        java.awt.image.BufferedImage rotated = new java.awt.image.BufferedImage(
                newWidth, newHeight, java.awt.image.BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = rotated.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.translate(newWidth / 2, newHeight / 2);
        g2d.rotate(Math.toRadians(degrees));
        g2d.translate(-width / 2, -height / 2);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    // -------------------------------------------------------------------------
    // Default center panel — "Make dishes" / "Find a dish" buttons
    // This is what you see on startup and after pressing "← Back"
    // -------------------------------------------------------------------------

    private JPanel createDefaultCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(UIColors.BORDER_ORANGE, 3, true));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        buttonPanel.setOpaque(false);

        JButton makeDishesButton = createStyledButton("Make dishes", UIColors.BUTTON_GREEN, new Color(200, 50, 50));
        makeDishesButton.setPreferredSize(new Dimension(280, 80));
        makeDishesButton.addActionListener(e -> openAddRecipeDialog());

        JButton findDishButton = createStyledButton("Find a dish", UIColors.BUTTON_RED, new Color(80, 80, 200));
        findDishButton.setPreferredSize(new Dimension(280, 80));
        findDishButton.addActionListener(e -> openFindDishDialog());

        buttonPanel.add(makeDishesButton);
        buttonPanel.add(findDishButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    // -------------------------------------------------------------------------
    // showRecipeInCenter — called by RecipeDetailDialog "Show the dish" button
    //
    // Replaces the white center area with:
    //   - A header bar showing the recipe name + "← Back" button
    //   - Left panel: ingredients grouped by category
    //   - Right panel: cooking procedure
    // -------------------------------------------------------------------------

    public void showRecipeInCenter(Recipe recipe) {
        JPanel recipeCard = new JPanel(new BorderLayout(0, 0));
        recipeCard.setBackground(Color.WHITE);
        recipeCard.setBorder(new LineBorder(UIColors.BORDER_ORANGE, 3, true));

        // --- Top bar: recipe name + back button ---
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(new Color(0xFF, 0xD6, 0x99));
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0xFF, 0x75, 0x1F)),
                new EmptyBorder(10, 16, 10, 16)
        ));

        JLabel nameLabel = new JLabel(recipe.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 28));
        nameLabel.setForeground(new Color(0xB0, 0x3A, 0x2E));
        topBar.add(nameLabel, BorderLayout.CENTER);

        // Back button — restores the default buttons view
        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> resetCenterPanel());
        topBar.add(backBtn, BorderLayout.WEST);

        recipeCard.add(topBar, BorderLayout.NORTH);

        // --- Center: ingredients left, procedure right ---
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildIngredientsPanel(recipe),
                buildProcedurePanel(recipe)
        );
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(8);
        splitPane.setResizeWeight(0.4);
        splitPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        recipeCard.add(splitPane, BorderLayout.CENTER);

        // Swap center panel
        mainPanel.remove(centerPanel);
        centerPanel = recipeCard;
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Builds the ingredients panel for the recipe card.
     * Groups by category, shows name in green.
     */
    private JPanel buildIngredientsPanel(Recipe recipe) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(new Color(0xFF, 0x75, 0x1F), 2));

        JLabel header = new JLabel("Ingredients", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD, 18));
        header.setForeground(Color.BLACK);
        header.setOpaque(true);
        header.setBackground(new Color(0xFF, 0xD6, 0x99));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0xFF, 0x75, 0x1F)),
                new EmptyBorder(8, 8, 8, 8)
        ));
        panel.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(new EmptyBorder(10, 14, 10, 14));

        // Group ingredients by category
        Map<String, List<Ingredient>> grouped = recipe.getIngredients().stream()
                .collect(Collectors.groupingBy(Ingredient::getCategory));

        for (Map.Entry<String, List<Ingredient>> entry : grouped.entrySet()) {
            // Category label
            JLabel catLabel = new JLabel(entry.getKey());
            catLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            catLabel.setForeground(new Color(0xB0, 0x3A, 0x2E));
            catLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(catLabel);
            listPanel.add(Box.createVerticalStrut(4));

            for (Ingredient ing : entry.getValue()) {
                JLabel ingLabel = new JLabel("• " + ing.getName());
                ingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                ingLabel.setForeground(new Color(0x16, 0x65, 0x34));
                ingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                listPanel.add(ingLabel);
                listPanel.add(Box.createVerticalStrut(4));
            }
            listPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Builds the procedure panel for the recipe card.
     */
    private JPanel buildProcedurePanel(Recipe recipe) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(new Color(0xFF, 0x75, 0x1F), 2));

        JLabel header = new JLabel("Cooking Procedure", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD, 18));
        header.setForeground(Color.BLACK);
        header.setOpaque(true);
        header.setBackground(new Color(0xFF, 0xD6, 0x99));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0xFF, 0x75, 0x1F)),
                new EmptyBorder(8, 8, 8, 8)
        ));
        panel.add(header, BorderLayout.NORTH);

        JTextArea procedureText = new JTextArea(recipe.getProcedure());
        procedureText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        procedureText.setLineWrap(true);
        procedureText.setWrapStyleWord(true);
        procedureText.setEditable(false);
        procedureText.setMargin(new Insets(12, 14, 12, 14));
        procedureText.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(procedureText);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Restores the default "Make dishes / Find a dish" center panel.
     * Called when the user presses "← Back" on the recipe card.
     */
    public void resetCenterPanel() {
        mainPanel.remove(centerPanel);
        centerPanel = createDefaultCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // -------------------------------------------------------------------------
    // Sidebar — unchanged from your original
    // -------------------------------------------------------------------------

    private JPanel createSidebar() {
        JPanel sidebarPanel = new JPanel(new BorderLayout(10, 10));
        sidebarPanel.setBackground(UIColors.SIDEBAR_BG);
        sidebarPanel.setPreferredSize(new Dimension(280, 0));
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIColors.BORDER_ORANGE, 3, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Your Dishes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(UIColors.ACCENT_RED);
        sidebarPanel.add(titleLabel, BorderLayout.NORTH);

        recipeListModel = new DefaultListModel<>();
        recipeList      = new JList<>(recipeListModel);
        recipeList.setFont(new Font("Segoe UI", Font.BOLD, 16));
        recipeList.setBackground(UIColors.SIDEBAR_BG);
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recipeList.setFixedCellHeight(80);
        recipeList.setBorder(new EmptyBorder(5, 5, 5, 5));

        recipeList.setCellRenderer(new ListCellRenderer<String>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends String> list, String value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.setOpaque(false);
                wrapper.setBorder(new EmptyBorder(5, 0, 5, 0));

                JPanel itemPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        if (isSelected) {
                            g2.setColor(new Color(255, 220, 180));
                        } else {
                            g2.setColor(UIColors.DISH_ITEM_BG);
                        }
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                        g2.setColor(UIColors.BORDER_LIGHT);
                        g2.setStroke(new BasicStroke(2));
                        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 40, 40);
                        g2.dispose();
                    }
                };

                itemPanel.setLayout(new BorderLayout());
                itemPanel.setOpaque(false);
                itemPanel.setPreferredSize(new Dimension(0, 60));

                JLabel label = new JLabel(value, SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 16));
                label.setForeground(Color.BLACK);

                itemPanel.add(label, BorderLayout.CENTER);
                wrapper.add(itemPanel, BorderLayout.CENTER);

                return wrapper;
            }
        });

        recipeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && recipeList.getSelectedIndex() != -1) {
                openRecipeViewer(recipeList.getSelectedIndex());
            }
        });

        JScrollPane scrollPane = new JScrollPane(recipeList);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new ThinScrollBarUI());
        verticalScrollBar.setPreferredSize(new Dimension(8, 0));
        verticalScrollBar.setUnitIncrement(16);

        sidebarPanel.add(scrollPane, BorderLayout.CENTER);

        return sidebarPanel;
    }

    private class ThinScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(150, 150, 150);
            this.trackColor = UIColors.SIDEBAR_BG;
        }
        @Override
        protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override
        protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(
                    thumbBounds.x + 1, thumbBounds.y + 2,
                    thumbBounds.width - 2, thumbBounds.height - 4,
                    6, 6
            );
            g2.dispose();
        }
    }

    // -------------------------------------------------------------------------
    // Styled button — unchanged from your original
    // -------------------------------------------------------------------------

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setColor(bgColor.darker().darker());
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 32));
        button.setForeground(fgColor);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        return button;
    }

    // -------------------------------------------------------------------------
    // Keyboard shortcuts + data loading — unchanged from your original
    // -------------------------------------------------------------------------

    private void setupKeyboardShortcuts() {
        KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlN, "newRecipe");
        mainPanel.getActionMap().put("newRecipe", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { openAddRecipeDialog(); }
        });

        KeyStroke ctrlF = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK);
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlF, "findDish");
        mainPanel.getActionMap().put("findDish", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { openFindDishDialog(); }
        });
    }

    private void loadRecipes() {
        try {
            recipeListModel.clear();
            List<Recipe> recipes = recipeManager.getAllRecipes();

            if (recipes.isEmpty()) {
                recipeListModel.addElement("dish #1");
                recipeListModel.addElement("dish #1");
                recipeListModel.addElement("dish #1");
            } else {
                for (Recipe recipe : recipes) {
                    recipeListModel.addElement(recipe.getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading recipes: " + e.getMessage());
            recipeListModel.addElement("Error loading");
        }
    }

    private void openAddRecipeDialog() {
        AddRecipeDialog dialog = new AddRecipeDialog(this);
        dialog.setVisible(true);
    }

    private void openFindDishDialog() {
        FindDishDialog dialog = new FindDishDialog(this);
        dialog.setVisible(true);
    }

    private void openRecipeViewer(int index) {
        try {
            List<Recipe> recipes = recipeManager.getAllRecipes();
            if (index >= 0 && index < recipes.size()) {
                Recipe selectedRecipe = recipes.get(index);
                ViewRecipeDialog dialog = new ViewRecipeDialog(this, selectedRecipe);
                dialog.setVisible(true);
            }
        } catch (Exception e) {
            System.err.println("Error opening recipe viewer: " + e.getMessage());
        }
    }

    public void refreshRecipeList() {
        loadRecipes();
    }
}
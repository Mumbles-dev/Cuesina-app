package com.cuesina.ui;

import com.cuesina.models.Ingredient;
import com.cuesina.models.Recipe;
import com.cuesina.models.RecipeMatchResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class RecipeDetailDialog extends JDialog {

    private RecipeMatchResult matchResult;
    private MainWindow mainWindow;       // reference to homescreen
    private JDialog findDishDialog;     // reference to FindDishDialog so we can close it too

    // Colors
    private static final Color BG_ORANGE       = new Color(0xFF, 0xBD, 0x59);
    private static final Color OUTLINE_ORANGE  = new Color(0xFF, 0x75, 0x1F);
    private static final Color INNER_HIGHLIGHT = new Color(0xFF, 0xDA, 0x8A);
    private static final Color HEADER_TAN      = new Color(0xFF, 0xD6, 0x99);
    private static final Color DIVIDER_BROWN   = new Color(0xCC, 0x7A, 0x00);
    private static final Color LABEL_BROWN     = new Color(0x7A, 0x44, 0x00);
    private static final Color TITLE_RED       = new Color(0xB0, 0x3A, 0x2E);
    private static final Color CLOSE_RED       = new Color(0xB0, 0x3A, 0x2E);
    private static final Color CLOSE_BORDER    = new Color(0x7A, 0x1F, 0x0F);
    private static final Color GREEN_TEXT      = new Color(0x16, 0x65, 0x34);
    private static final Color RED_TEXT        = new Color(0x99, 0x1B, 0x1B);
    private static final Color BTN_GREEN       = new Color(0x3A, 0x7D, 0x2C);
    private static final Color BTN_BORDER      = new Color(0x1F, 0x4D, 0x18);

    /**
     * Full constructor — used by FindDishDialog
     *
     * @param parent         the FindDishDialog that opened this
     * @param result         the match result to display
     * @param mainWindow     reference to MainWindow for showRecipeInCenter()
     * @param findDishDialog reference to FindDishDialog so we can close it on "Show the dish"
     */
    public RecipeDetailDialog(JDialog parent, RecipeMatchResult result,
                              MainWindow mainWindow, JDialog findDishDialog) {
        super(parent, true);
        this.matchResult    = result;
        this.mainWindow     = mainWindow;
        this.findDishDialog = findDishDialog;

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setSize(800, 520);
        setLocationRelativeTo(parent);

        createUI();
    }

    /**
     * Backward-compatible constructor (no mainWindow, no findDishDialog)
     */
    public RecipeDetailDialog(JDialog parent, RecipeMatchResult result) {
        this(parent, result, null, null);
    }

    // -------------------------------------------------------------------------
    // UI Construction
    // -------------------------------------------------------------------------

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Orange background
                g2.setColor(BG_ORANGE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Outer border
                g2.setColor(OUTLINE_ORANGE);
                g2.setStroke(new BasicStroke(3));
                g2.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
                // Inner highlight
                g2.setColor(INNER_HIGHLIGHT);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(5, 5, getWidth() - 10, getHeight() - 10);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        mainPanel.add(buildHeader(),  BorderLayout.NORTH);
        mainPanel.add(buildContent(), BorderLayout.CENTER);
        mainPanel.add(buildFooter(),  BorderLayout.SOUTH);

        JButton closeButton = createCloseButton();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 520));

        mainPanel.setBounds(0, 0, 800, 520);
        closeButton.setBounds(754, 12, 30, 30);

        layeredPane.add(mainPanel,   JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(closeButton, JLayeredPane.PALETTE_LAYER);

        setContentPane(layeredPane);
    }

    // -------------------------------------------------------------------------
    // Header — title + full-width decorative divider
    // -------------------------------------------------------------------------

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel titleLabel = new JLabel(matchResult.getRecipe().getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 44));
        titleLabel.setForeground(TITLE_RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(titleLabel);

        header.add(Box.createVerticalStrut(10));

        // Full-width divider — custom painted so lines stretch to full width
        JPanel dividerRow = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(DIVIDER_BROWN);
                g2.setStroke(new BasicStroke(1.5f));

                String text     = "ingredients checklist";
                Font font       = new Font("Serif", Font.ITALIC, 13);
                FontMetrics fm  = g2.getFontMetrics(font);
                int textW       = fm.stringWidth(text);
                int cx          = getWidth() / 2;
                int cy          = getHeight() / 2;

                // Left and right lines stretch to edges
                g2.drawLine(0, cy, cx - textW / 2 - 8, cy);
                g2.drawLine(cx + textW / 2 + 8, cy, getWidth(), cy);

                // Centered label
                g2.setColor(LABEL_BROWN);
                g2.setFont(font);
                g2.drawString(text, cx - textW / 2, cy + fm.getAscent() / 2 - 1);

                g2.dispose();
            }
        };
        dividerRow.setOpaque(false);
        dividerRow.setPreferredSize(new Dimension(0, 20));
        dividerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        dividerRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(dividerRow);

        return header;
    }

    // -------------------------------------------------------------------------
    // Content — two ingredient panels side by side
    // -------------------------------------------------------------------------

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);
        content.add(createCurrentIngredientsPanel());
        content.add(createMissingIngredientsPanel());
        return content;
    }

    // -------------------------------------------------------------------------
    // Footer — pill-shaped "Show the dish" button
    // -------------------------------------------------------------------------

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(14, 0, 0, 0));

        // Pill shape painted inside paintComponent — no external LineBorder
        JButton showDishBtn = new JButton("Show the dish") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = getHeight(); // full arc = perfect pill

                g2.setColor(getModel().isPressed()  ? BTN_GREEN.darker()
                        : getModel().isRollover() ? BTN_GREEN.brighter()
                        : BTN_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                // Border drawn inside — follows pill shape
                g2.setColor(BTN_BORDER);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);

                g2.dispose();
                super.paintComponent(g); // draws text on top
            }
        };
        showDishBtn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        showDishBtn.setForeground(Color.WHITE);
        showDishBtn.setContentAreaFilled(false); // we paint it
        showDishBtn.setBorderPainted(false);     // we paint the border
        showDishBtn.setFocusPainted(false);
        showDishBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showDishBtn.setPreferredSize(new Dimension(200, 42));

        showDishBtn.addActionListener(e -> {
            Recipe recipe = matchResult.getRecipe();

            dispose(); // close RecipeDetailDialog

            if (findDishDialog != null) {
                findDishDialog.dispose(); // close FindDishDialog
            }

            if (mainWindow != null) {
                mainWindow.setVisible(true);
                mainWindow.toFront();
                mainWindow.showRecipeInCenter(recipe); // show recipe on homescreen
            }
        });

        footer.add(showDishBtn);
        return footer;
    }

    // -------------------------------------------------------------------------
    // Close button
    // -------------------------------------------------------------------------

    private JButton createCloseButton() {
        JButton btn = new JButton("×") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CLOSE_RED);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(CLOSE_BORDER);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> dispose());
        return btn;
    }

    // -------------------------------------------------------------------------
    // Ingredient panels
    // -------------------------------------------------------------------------

    private JPanel createCurrentIngredientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(Color.BLACK, 2));

        JLabel header = new JLabel("Current Ingredients", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD, 18));
        header.setForeground(Color.BLACK);
        header.setOpaque(true);
        header.setBackground(HEADER_TAN);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK),
                new EmptyBorder(10, 8, 10, 8)
        ));
        panel.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(new EmptyBorder(10, 14, 10, 14));

        if (matchResult.getMatchedIngredients().isEmpty()) {
            JLabel empty = new JLabel("None");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            empty.setForeground(Color.GRAY);
            listPanel.add(empty);
        } else {
            for (Ingredient ingredient : matchResult.getMatchedIngredients()) {
                JLabel lbl = new JLabel("• " + ingredient.getName());
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lbl.setForeground(GREEN_TEXT);
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                listPanel.add(lbl);
                listPanel.add(Box.createVerticalStrut(6));
            }
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMissingIngredientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(Color.BLACK, 2));

        JLabel header = new JLabel("Missing Ingredients", SwingConstants.CENTER);
        header.setFont(new Font("Serif", Font.BOLD, 18));
        header.setForeground(Color.BLACK);
        header.setOpaque(true);
        header.setBackground(HEADER_TAN);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK),
                new EmptyBorder(10, 8, 10, 8)
        ));
        panel.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(new EmptyBorder(10, 14, 10, 14));

        if (matchResult.getMissingIngredients().isEmpty()) {
            JLabel lbl = new JLabel("✓ You have everything!");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lbl.setForeground(new Color(0, 150, 0));
            listPanel.add(lbl);
        } else {
            for (Ingredient ingredient : matchResult.getMissingIngredients()) {
                JLabel lbl = new JLabel("• " + ingredient.getName());
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lbl.setForeground(RED_TEXT);
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                listPanel.add(lbl);
                listPanel.add(Box.createVerticalStrut(6));
            }
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}
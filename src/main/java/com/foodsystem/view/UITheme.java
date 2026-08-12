package com.foodsystem.view;

import javax.swing.*;
import java.awt.*;

/**
 * UITheme holds every color and font used across the application, in
 * one place. Every screen (yours and your teammates') should pull
 * colors/fonts from here instead of typing new Color(...) values
 * directly - that's what makes the whole app look like ONE product
 * instead of four screens stitched together in different styles.
 */
public class UITheme {

    // Color palette - a simple green/cream theme fitting a food/community system
    public static final Color PRIMARY = new Color(46, 125, 50);      // dark green - buttons, headers
    public static final Color PRIMARY_DARK = new Color(27, 94, 32);  // hover/pressed state
    public static final Color ACCENT = new Color(255, 152, 0);       // orange - highlights, warnings
    public static final Color BACKGROUND = new Color(245, 245, 240); // light cream page background
    public static final Color SIDEBAR = new Color(33, 49, 33);       // dark green sidebar
    public static final Color TEXT_DARK = new Color(33, 33, 33);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color ERROR = new Color(198, 40, 40);

    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);

    /** Applies the standard "primary" look to a button (green, white text, no ugly border). */
    public static void stylePrimaryButton(JButton button) {
        button.setBackground(PRIMARY);
        button.setForeground(TEXT_LIGHT);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /** Applies a plain text-link look to a button (used for "Don't have an account? Register"). */
    public static void styleLinkButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(PRIMARY);
        button.setFont(LABEL_FONT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /** Applies consistent padding/font to a text field. */
    public static void styleTextField(JTextField field) {
        field.setFont(LABEL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    /** Applies consistent styling to a sidebar navigation button in DashboardFrame. */
    public static void styleSidebarButton(JButton button) {
        button.setForeground(TEXT_LIGHT);
        button.setBackground(SIDEBAR);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
}

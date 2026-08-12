package com.foodsystem.view;

import com.foodsystem.exception.EmptyFieldException;
import com.foodsystem.model.User;
import com.foodsystem.storage.UserFileManager;
import com.foodsystem.util.Validator;

import javax.swing.*;
import java.awt.*;

/**
 * LoginFrame is the first screen the user sees. It only has two jobs:
 *   1. Collect email + password and validate they aren't empty.
 *   2. Ask UserFileManager whether those credentials match a real
 *      account (stored in data/users.csv), and if so, open
 *      DashboardFrame and close this window.
 *
 * All the "is this really a valid user" logic lives in
 * UserFileManager, not here - this class is purely the View + a thin
 * event handler.
 */
public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private final UserFileManager userFileManager = new UserFileManager();

    public LoginFrame() {
        setTitle("Community Food Donation and Request System - Login");
        setSize(420, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centers the window on screen
        setResizable(false);

        add(buildContent());
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 40, 40, 40));

        JLabel title = new JLabel("Welcome Back");
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(UITheme.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Log in to continue");
        subtitle.setFont(UITheme.LABEL_FONT);
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(320, 40));
        UITheme.styleTextField(emailField);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(320, 40));
        UITheme.styleTextField(passwordField);

        JButton loginButton = new JButton("Log In");
        UITheme.stylePrimaryButton(loginButton);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());

        JButton registerLink = new JButton("Don't have an account? Register here");
        UITheme.styleLinkButton(registerLink);
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose(); // close the login window while registering
        });

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(labeled("Email", emailField));
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(labeled("Password", passwordField));
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(loginButton);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(registerLink);

        return panel;
    }

    /** Small helper: stacks a label above a field, both left-aligned, with consistent width. */
    private JPanel labeled(String labelText, JComponent field) {
        JPanel wrapper = new JPanel();
        wrapper.setBackground(UITheme.BACKGROUND);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(320, 70));

        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.LABEL_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(label);
        wrapper.add(Box.createRigidArea(new Dimension(0, 4)));
        wrapper.add(field);
        return wrapper;
    }

    /**
     * Event handler for the Log In button. Follows the standard
     * pattern used across the whole project:
     *   1. validate input (throws a checked exception on failure)
     *   2. call the DAO
     *   3. on success, move to the next screen
     *   4. on any failure, show a JOptionPane, don't crash
     */
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            Validator.validateNotEmpty(email, "Email");
            Validator.validateNotEmpty(password, "Password");

            User user = userFileManager.findByCredentials(email, password);

            if (user == null) {
                JOptionPane.showMessageDialog(this,
                        "Invalid email or password. Please try again.",
                        "Login failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Welcome back, " + user.getName() + "!",
                    "Login successful", JOptionPane.INFORMATION_MESSAGE);

            new DashboardFrame(user).setVisible(true);
            dispose(); // close the login window now that we're logged in

        } catch (EmptyFieldException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Input error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

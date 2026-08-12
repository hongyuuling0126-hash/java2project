package com.foodsystem.view;

import com.foodsystem.exception.DuplicateRecordException;
import com.foodsystem.exception.EmptyFieldException;
import com.foodsystem.exception.InvalidInputException;
import com.foodsystem.model.User;
import com.foodsystem.storage.UserFileManager;
import com.foodsystem.util.Validator;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

/**
 * RegisterFrame lets a new user create an account. On success it sends
 * them back to LoginFrame to log in with their new credentials (rather
 * than logging them straight in) - this is a common, simple pattern
 * and keeps the login flow as the single place a session actually starts.
 */
public class RegisterFrame extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private final UserFileManager userFileManager = new UserFileManager();

    public RegisterFrame() {
        setTitle("Community Food Donation and Request System - Register");
        setSize(420, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        add(buildContent());
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Create Account");
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(UITheme.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = new JTextField();
        emailField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        for (JTextField f : new JTextField[]{nameField, emailField, passwordField, confirmPasswordField}) {
            f.setMaximumSize(new Dimension(320, 40));
            UITheme.styleTextField(f);
        }

        JButton registerButton = new JButton("Register");
        UITheme.stylePrimaryButton(registerButton);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> handleRegister());

        JButton loginLink = new JButton("Already have an account? Log in");
        UITheme.styleLinkButton(loginLink);
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLink.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(labeled("Full name", nameField));
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(labeled("Email", emailField));
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(labeled("Password", passwordField));
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(labeled("Confirm password", confirmPasswordField));
        panel.add(Box.createRigidArea(new Dimension(0, 26)));
        panel.add(registerButton);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(loginLink);

        return panel;
    }

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
     * Event handler for the Register button. A few more checks than
     * login has: every field must be filled in, the email must look
     * like an email, and the two password fields must match.
     */
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        try {
            Validator.validateNotEmpty(name, "Full name");
            Validator.validateNotEmpty(email, "Email");
            Validator.validateNotEmpty(password, "Password");
            Validator.validateEmailFormat(email);

            if (!password.equals(confirmPassword)) {
                throw new InvalidInputException("Passwords do not match.");
            }

            // Simple unique id generation - good enough for a class
            // project. UUID.randomUUID() produces a long random string;
            // we just take the first 8 characters to keep ids short and
            // readable, e.g. "U-3f9a2b1c".
            String id = "U-" + UUID.randomUUID().toString().substring(0, 8);
            User newUser = new User(id, name, email, password, "USER");

            userFileManager.save(newUser);

            JOptionPane.showMessageDialog(this,
                    "Account created successfully! Please log in.",
                    "Registration successful", JOptionPane.INFORMATION_MESSAGE);

            new LoginFrame().setVisible(true);
            dispose();

        } catch (EmptyFieldException | InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Input error", JOptionPane.ERROR_MESSAGE);
        } catch (DuplicateRecordException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Registration failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}

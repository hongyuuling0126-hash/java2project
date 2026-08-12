package com.foodsystem;

import com.foodsystem.view.LoginFrame;

import javax.swing.*;

/**
 * Main is the application's entry point - the one method that runs
 * when the whole program starts. Its only job is to show LoginFrame.
 *
 * SwingUtilities.invokeLater(...) is standard practice for starting
 * any Swing application: it makes sure the GUI is built and shown on
 * Swing's own dedicated thread (the "Event Dispatch Thread"), rather
 * than whatever thread main() happens to run on. You don't need to
 * fully understand thread mechanics to use this correctly - just
 * always start a Swing app this way.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

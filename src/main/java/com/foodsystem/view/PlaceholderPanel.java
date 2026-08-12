package com.foodsystem.view;

import javax.swing.*;
import java.awt.*;

/**
 * PlaceholderPanel is a temporary stand-in for a screen a teammate
 * hasn't built yet (DonationFormPanel, RequestFormPanel, AdminPanel).
 * It lets DashboardFrame's navigation work correctly TODAY, so you can
 * finish and test your module without waiting on anyone else.
 *
 * When Member 2/3/4 finish their real panel, DashboardFrame just swaps
 * `new PlaceholderPanel("Donation Module")` for `new DonationFormPanel(user)`
 * - nothing else about the navigation needs to change.
 */
public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String moduleName) {
        setBackground(UITheme.BACKGROUND);
        setLayout(new GridBagLayout());

        JLabel label = new JLabel(moduleName + " - coming soon");
        label.setFont(UITheme.HEADER_FONT);
        label.setForeground(Color.GRAY);

        add(label);
    }
}

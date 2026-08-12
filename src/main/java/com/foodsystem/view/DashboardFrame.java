package com.foodsystem.view;

import com.foodsystem.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * DashboardFrame is the main window of the whole application. It opens
 * right after a successful login and stays open for the rest of the
 * session - every other module's screen (donation, request, admin)
 * appears INSIDE this window, swapped in and out using CardLayout,
 * rather than opening as separate pop-up windows.
 *
 * How CardLayout works, in plain terms: imagine a physical stack of
 * index cards, all the same size, sitting on top of each other. Only
 * the top card is visible at any moment. CardLayout.show(container,
 * "cardName") flips to whichever card has that name. That's the whole
 * mechanism - it's simpler than it sounds.
 */
public class DashboardFrame extends JFrame {

    private static final String CARD_DONATION = "DONATION";
    private static final String CARD_REQUEST = "REQUEST";
    private static final String CARD_ADMIN = "ADMIN";

    private final User loggedInUser;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardContainer = new JPanel(cardLayout);

    public DashboardFrame(User loggedInUser) {
        this.loggedInUser = loggedInUser;

        setTitle("Community Food Donation and Request System - Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(760, 480));

        setLayout(new BorderLayout());
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildCardContainer(), BorderLayout.CENTER);
    }

    /** Top bar: welcome message + logout button. */
    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.PRIMARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel welcome = new JLabel("Welcome, " + loggedInUser.getName()
                + (loggedInUser.isAdmin() ? "  (Admin)" : ""));
        welcome.setFont(UITheme.HEADER_FONT);
        welcome.setForeground(UITheme.TEXT_LIGHT);

        JButton logoutButton = new JButton("Log Out");
        UITheme.styleLinkButton(logoutButton);
        logoutButton.setForeground(UITheme.TEXT_LIGHT);
        logoutButton.addActionListener(e -> handleLogout());

        topBar.add(welcome, BorderLayout.WEST);
        topBar.add(logoutButton, BorderLayout.EAST);
        return topBar;
    }

    /**
     * Sidebar: one navigation button per module. The Admin button only
     * appears if loggedInUser.isAdmin() is true - this is the simplest
     * form of "role-based" navigation: an everyday USER never even sees
     * the option to open the admin screen.
     */
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton donationButton = new JButton("Donate Food");
        UITheme.styleSidebarButton(donationButton);
        donationButton.addActionListener(e -> cardLayout.show(cardContainer, CARD_DONATION));

        JButton requestButton = new JButton("Request Food");
        UITheme.styleSidebarButton(requestButton);
        requestButton.addActionListener(e -> cardLayout.show(cardContainer, CARD_REQUEST));

        sidebar.add(donationButton);
        sidebar.add(requestButton);

        if (loggedInUser.isAdmin()) {
            JButton adminButton = new JButton("Admin Panel");
            UITheme.styleSidebarButton(adminButton);
            adminButton.addActionListener(e -> cardLayout.show(cardContainer, CARD_ADMIN));
            sidebar.add(adminButton);
        }

        return sidebar;
    }

    /**
     * Builds the CardLayout container and registers each module's panel
     * under a name. Right now every card is a PlaceholderPanel -
     * replace each "new PlaceholderPanel(...)" line with the real panel
     * class as your teammates finish their modules, e.g.:
     *
     *   cardContainer.add(new DonationFormPanel(loggedInUser), CARD_DONATION);
     */
    private JPanel buildCardContainer() {
        cardContainer.setBackground(UITheme.BACKGROUND);

        cardContainer.add(new PlaceholderPanel("Donation Module"), CARD_DONATION);
        cardContainer.add(new RequestFormPanel(loggedInUser), CARD_REQUEST);

        if (loggedInUser.isAdmin()) {
            cardContainer.add(new PlaceholderPanel("Admin Panel"), CARD_ADMIN);
        }

        // Show the donation card first by default when the dashboard opens.
        cardLayout.show(cardContainer, CARD_DONATION);

        return cardContainer;
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?", "Confirm logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}

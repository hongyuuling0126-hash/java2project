package com.foodsystem.view;

import com.foodsystem.exception.EmptyFieldException;
import com.foodsystem.exception.InvalidInputException;
import com.foodsystem.model.Request;
import com.foodsystem.model.Status;
import com.foodsystem.model.Urgency;
import com.foodsystem.model.User;
import com.foodsystem.storage.RequestFileManager;
import com.foodsystem.util.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * RequestFormPanel is Member 3's module: it lets the logged-in user
 * submit a food assistance request (with an urgency level chosen via
 * radio buttons) and shows a live table of everything THEY have
 * submitted so far.
 *
 * Layout: a form on the left (inputs + submit/clear buttons), a table
 * on the right (their request history). Submitting a new request
 * immediately refreshes the table - that's the "event handling"
 * requirement in action: one button click updates both the file on
 * disk and the on-screen table in the same action.
 */
public class RequestFormPanel extends JPanel {

    private final User loggedInUser;
    private final RequestFileManager requestFileManager = new RequestFileManager();

    private JTextField foodItemField;
    private JTextField quantityField;
    private JTextArea reasonArea;
    private JRadioButton lowRadio;
    private JRadioButton mediumRadio;
    private JRadioButton highRadio;

    private DefaultTableModel tableModel;
    private JTable requestTable;

    public RequestFormPanel(User loggedInUser) {
        this.loggedInUser = loggedInUser;

        setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Request Food Assistance");
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(UITheme.TEXT_DARK);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setBackground(UITheme.BACKGROUND);
        content.add(buildFormPanel());
        content.add(buildTablePanel());

        add(title, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);

        refreshTable(); // show existing requests as soon as the panel opens
    }

    // ---------------------------------------------------------------
    // Form (left side)
    // ---------------------------------------------------------------

    private JPanel buildFormPanel() {
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        foodItemField = new JTextField();
        quantityField = new JTextField();
        reasonArea = new JTextArea(4, 20);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setFont(UITheme.LABEL_FONT);
        reasonArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        UITheme.styleTextField(foodItemField);
        UITheme.styleTextField(quantityField);
        foodItemField.setAlignmentX(Component.LEFT_ALIGNMENT);
        quantityField.setAlignmentX(Component.LEFT_ALIGNMENT);
        foodItemField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        quantityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Radio buttons for urgency - a ButtonGroup makes them mutually
        // exclusive (selecting one automatically deselects the others).
        // Medium starts pre-selected so there is always a valid choice,
        // even if the user submits without touching the radio buttons.
        lowRadio = new JRadioButton("Low");
        mediumRadio = new JRadioButton("Medium", true);
        highRadio = new JRadioButton("High");
        for (JRadioButton r : new JRadioButton[]{lowRadio, mediumRadio, highRadio}) {
            r.setBackground(Color.WHITE);
            r.setFont(UITheme.LABEL_FONT);
            r.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        ButtonGroup urgencyGroup = new ButtonGroup();
        urgencyGroup.add(lowRadio);
        urgencyGroup.add(mediumRadio);
        urgencyGroup.add(highRadio);

        JPanel urgencyRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        urgencyRow.setBackground(Color.WHITE);
        urgencyRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        urgencyRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        urgencyRow.add(lowRadio);
        urgencyRow.add(mediumRadio);
        urgencyRow.add(highRadio);

        JButton submitButton = new JButton("Submit Request");
        UITheme.stylePrimaryButton(submitButton);
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.addActionListener(e -> handleSubmit());

        JButton clearButton = new JButton("Clear Form");
        UITheme.styleLinkButton(clearButton);
        clearButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearButton.addActionListener(e -> clearForm());

        form.add(sectionLabel("Food item"));
        form.add(foodItemField);
        form.add(Box.createRigidArea(new Dimension(0, 14)));
        form.add(sectionLabel("Quantity"));
        form.add(quantityField);
        form.add(Box.createRigidArea(new Dimension(0, 14)));
        form.add(sectionLabel("Reason"));
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        reasonScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        form.add(reasonScroll);
        form.add(Box.createRigidArea(new Dimension(0, 14)));
        form.add(sectionLabel("Urgency"));
        form.add(urgencyRow);
        form.add(Box.createRigidArea(new Dimension(0, 20)));
        form.add(submitButton);
        form.add(Box.createRigidArea(new Dimension(0, 8)));
        form.add(clearButton);

        return form;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.LABEL_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return label;
    }

    // ---------------------------------------------------------------
    // Table (right side)
    // ---------------------------------------------------------------

    private JPanel buildTablePanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));

        JLabel header = new JLabel("My Submitted Requests");
        header.setFont(UITheme.HEADER_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(14, 14, 10, 14));

        String[] columns = {"Food Item", "Qty", "Urgency", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table - users view records, they don't edit cells directly
            }
        };
        requestTable = new JTable(tableModel);
        requestTable.setRowHeight(28);
        requestTable.setFont(UITheme.LABEL_FONT);
        requestTable.getTableHeader().setFont(UITheme.BUTTON_FONT);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(requestTable), BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Reloads the table from disk - called once when the panel is
     * first built, and again every time a new request is submitted, so
     * the table always reflects what's actually saved.
     */
    private void refreshTable() {
        tableModel.setRowCount(0); // clear all existing rows first

        List<Request> myRequests = requestFileManager.findByUserId(loggedInUser.getId());
        for (Request r : myRequests) {
            tableModel.addRow(new Object[]{
                    r.getFoodItem(),
                    r.getQuantity(),
                    r.getUrgency(),
                    r.getDateSubmitted(),
                    r.getStatus()
            });
        }
    }

    // ---------------------------------------------------------------
    // Event handlers
    // ---------------------------------------------------------------

    /**
     * Submit button handler - the standard project pattern:
     * validate -> build the object -> save -> give feedback -> refresh.
     */
    private void handleSubmit() {
        String foodItem = foodItemField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String reason = reasonArea.getText().trim();

        try {
            Validator.validateNotEmpty(foodItem, "Food item");
            int quantity = Validator.parsePositiveInt(quantityText, "Quantity");
            Validator.validateNotEmpty(reason, "Reason");
            Validator.validateNoCommas(reason, "Reason");

            Urgency urgency = getSelectedUrgency();

            String id = "R-" + UUID.randomUUID().toString().substring(0, 8);
            Request request = new Request(id, loggedInUser.getId(), foodItem, quantity,
                    reason, urgency, LocalDate.now().toString(), Status.PENDING);

            requestFileManager.save(request);

            JOptionPane.showMessageDialog(this,
                    "Your request has been submitted and is now pending review.",
                    "Request submitted", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            refreshTable();

        } catch (EmptyFieldException | InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Input error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Urgency getSelectedUrgency() {
        if (lowRadio.isSelected()) return Urgency.LOW;
        if (highRadio.isSelected()) return Urgency.HIGH;
        return Urgency.MEDIUM; // default/fallback - mediumRadio starts pre-selected
    }

    private void clearForm() {
        foodItemField.setText("");
        quantityField.setText("");
        reasonArea.setText("");
        mediumRadio.setSelected(true);
    }
}

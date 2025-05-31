package com.studentinfo;

import javax.swing.*;
import java.awt.*;


/**
 * Department management panel with department ID, name, location, and phone input.
 * Handles adding, editing, and searching department records.
 */
class DepartmentPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField idField, nameField, locationField, phoneField;
    private JButton addButton, searchButton, editButton, resetButton;
    private JLabel statusLabel;

    public DepartmentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Department ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Location:"));
        locationField = new JTextField();
        formPanel.add(locationField);

        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        statusLabel = new JLabel(" ");
        formPanel.add(statusLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add Department");
        searchButton = new JButton("Search");
        editButton = new JButton("Edit Department");
        resetButton = new JButton("Reset");

        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(editButton);
        buttonPanel.add(resetButton);

        // Add components to main panel
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> addDepartment());
        searchButton.addActionListener(e -> searchDepartment());
        editButton.addActionListener(e -> editDepartment());
        resetButton.addActionListener(e -> resetFields());
    }

    private void addDepartment() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String phone = phoneField.getText().trim();

            // Validate fields
            if (name.isEmpty() || location.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if department ID already exists
            if (mainFrame.getDepartments().size() > 0 && mainFrame.findDepartmentById(id) != null) {
                JOptionPane.showMessageDialog(this, "Department ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create new department and add to list
            Department department = new Department(id, name, location, phone);
            mainFrame.getDepartments().add(department);
            mainFrame.saveDepartments();

            // Update department combos in other panels
            mainFrame.refreshDepartmentCombos();

            JOptionPane.showMessageDialog(this, "Department added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            resetFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchDepartment() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Department department = mainFrame.findDepartmentById(id);

            if (department != null) {
                idField.setText(String.valueOf(department.getId()));
                nameField.setText(department.getName());
                locationField.setText(department.getLocation());
                phoneField.setText(department.getPhone());
                statusLabel.setText("Department found.");
            } else {
                JOptionPane.showMessageDialog(this, "Department ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editDepartment() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Department department = mainFrame.findDepartmentById(id);

            if (department != null) {
                // Lock ID field
                idField.setEditable(false);
                
                // Update department information
                String name = nameField.getText().trim();
                String location = locationField.getText().trim();
                String phone = phoneField.getText().trim();

                // Validate fields
                if (name.isEmpty() || location.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                department.setName(name);
                department.setLocation(location);
                department.setPhone(phone);
                mainFrame.saveDepartments();

                JOptionPane.showMessageDialog(this, "Department updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetFields();
            } else {
                JOptionPane.showMessageDialog(this, "Department ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        idField.setText("");
        nameField.setText("");
        locationField.setText("");
        phoneField.setText("");
        statusLabel.setText(" ");
        
        // Ensure ID field is editable and has normal background color
        idField.setEditable(true);
        idField.setBackground(UIManager.getColor("TextField.background"));
    }
} 
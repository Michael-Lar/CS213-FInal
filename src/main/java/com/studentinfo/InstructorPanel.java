package com.studentinfo;

import javax.swing.*;
import java.awt.*;


/**
 * Instructor management panel with instructor ID, name, email, phone, and department assignment.
 * Handles adding, editing, and searching instructor records.
 */
class InstructorPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField idField, nameField, emailField, phoneField;
    private JComboBox<Department> departmentCombo;
    private JButton addButton, searchButton, editButton, resetButton;
    private JLabel statusLabel;

    public InstructorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Instructor ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Department:"));
        departmentCombo = new JComboBox<>();
        updateDepartmentCombo();
        formPanel.add(departmentCombo);

        statusLabel = new JLabel(" ");
        formPanel.add(statusLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add Instructor");
        searchButton = new JButton("Search");
        editButton = new JButton("Edit Instructor");
        resetButton = new JButton("Reset");

        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(editButton);
        buttonPanel.add(resetButton);

        // Add components to main panel
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> addInstructor());
        searchButton.addActionListener(e -> searchInstructor());
        editButton.addActionListener(e -> editInstructor());
        resetButton.addActionListener(e -> resetFields());
    }

    public void updateDepartmentCombo() {
        departmentCombo.removeAllItems();
        for (int i = 0; i < mainFrame.getDepartments().size(); i++) {
            departmentCombo.addItem(mainFrame.getDepartments().get(i));
        }
    }

    private void addInstructor() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            Department selectedDepartment = (Department) departmentCombo.getSelectedItem();

            // Validate fields
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || selectedDepartment == null) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if instructor ID already exists
            if (mainFrame.getInstructors().size() > 0 && mainFrame.findInstructorById(id) != null) {
                JOptionPane.showMessageDialog(this, "Instructor ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create new instructor and add to list
            Instructor instructor = new Instructor(id, name, email, phone, selectedDepartment.getId());
            mainFrame.getInstructors().add(instructor);
            mainFrame.saveInstructors();

            // Refresh instructor combos in other panels
            mainFrame.refreshInstructorCombos();

            JOptionPane.showMessageDialog(this, "Instructor added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            resetFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchInstructor() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Instructor instructor = mainFrame.findInstructorById(id);

            if (instructor != null) {
                idField.setText(String.valueOf(instructor.getId()));
                nameField.setText(instructor.getName());
                emailField.setText(instructor.getEmail());
                phoneField.setText(instructor.getPhone());
                
                // Set department in combo box
                Department department = mainFrame.findDepartmentById(instructor.getDepartmentId());
                if (department != null) {
                    for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                        if (departmentCombo.getItemAt(i).getId() == department.getId()) {
                            departmentCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
                
                statusLabel.setText("Instructor found.");
            } else {
                JOptionPane.showMessageDialog(this, "Instructor ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editInstructor() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Instructor instructor = mainFrame.findInstructorById(id);

            if (instructor != null) {
                // Lock ID field
                idField.setEditable(false);
                
                // Update instructor information
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                Department selectedDepartment = (Department) departmentCombo.getSelectedItem();

                // Validate fields
                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || selectedDepartment == null) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                instructor.setName(name);
                instructor.setEmail(email);
                instructor.setPhone(phone);
                instructor.setDepartmentId(selectedDepartment.getId());
                mainFrame.saveInstructors();

                JOptionPane.showMessageDialog(this, "Instructor updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetFields();
            } else {
                JOptionPane.showMessageDialog(this, "Instructor ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        departmentCombo.setSelectedIndex(0);
        statusLabel.setText(" ");
        
        // Ensure ID field is editable and has normal background color
        idField.setEditable(true);
        idField.setBackground(UIManager.getColor("TextField.background"));
    }
} 
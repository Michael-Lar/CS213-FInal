package com.studentinfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.studentinfo.dao.DepartmentDAO;

/**
 * Department management panel with department ID, name, location, and phone input.
 * Handles adding, editing, and searching department records and displays them in a JTable.
 */
class DepartmentPanel extends JPanel {
    private MainFrame mainFrame;
    private DepartmentDAO departmentDAO;
    private JTextField idField, nameField, locationField, phoneField;
    private JButton addButton, searchButton, editButton, deleteButton, resetButton;
    private JLabel statusLabel;
    private JTable departmentTable;
    private DefaultTableModel tableModel;

    public DepartmentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.departmentDAO = new DepartmentDAO(); // Initialize DepartmentDAO
        setupUI();
        refreshDepartmentTable(); // Load initial data
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10)); // Added gaps
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Added padding

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout()); // Using GridBagLayout for more flexibility
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Department ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; idField = new JTextField(15); idField.setEditable(false); formPanel.add(idField, gbc); // ID not editable for add, shown on select

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; nameField = new JTextField(15); formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; locationField = new JTextField(15); formPanel.add(locationField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; phoneField = new JTextField(15); formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; statusLabel = new JLabel(" "); formPanel.add(statusLabel, gbc);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Departments"));
        String[] columnNames = {"ID", "Name", "Location", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells not editable
            }
        };
        departmentTable = new JTable(tableModel);
        departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        departmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && departmentTable.getSelectedRow() != -1) {
                populateFieldsFromSelectedRow();
            }
        });
        JScrollPane scrollPane = new JScrollPane(departmentTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Add Department");
        searchButton = new JButton("Search by ID");
        editButton = new JButton("Update Department");
        deleteButton = new JButton("Delete Department");
        resetButton = new JButton("Reset Fields");

        // Add buttons in a specific order
        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetButton);

        // Add components to main panel
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(formPanel, BorderLayout.NORTH);
        northPanel.add(buttonPanel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(e -> addDepartment());
        searchButton.addActionListener(e -> searchDepartment());
        editButton.addActionListener(e -> updateDepartment());
        deleteButton.addActionListener(e -> deleteDepartment());
        resetButton.addActionListener(e -> resetFields());
        
        idField.setToolTipText("Department ID (auto-generated, shown for selected department)");
    }

    private void populateFieldsFromSelectedRow() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            locationField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            phoneField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            statusLabel.setText("Selected Department ID: " + idField.getText());
            idField.setEditable(false); // ID is not editable directly
        }
    }

    public void refreshDepartmentTable() {
        tableModel.setRowCount(0); // Clear existing data
        List<Department> departments = departmentDAO.getAllDepartments();
        for (Department dept : departments) {
            tableModel.addRow(new Object[]{dept.getId(), dept.getName(), dept.getLocation(), dept.getPhone()});
        }
        statusLabel.setText("Table refreshed. " + departments.size() + " departments.");
    }

    private void addDepartment() {
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || location.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Location, and Phone are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Department department = new Department(0, name, location, phone); // ID is 0 as it's auto-generated
        try {
            if (departmentDAO.addDepartment(department)) {
                JOptionPane.showMessageDialog(this, "Department added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDepartmentTable();
                mainFrame.refreshDepartmentCombos(); // Update combos in other panels
                resetFields();
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchDepartment() {
        String idText = JOptionPane.showInputDialog(this, "Enter Department ID to search:");
        if (idText == null || idText.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(idText.trim());
            Department department = departmentDAO.getDepartmentById(id);

            if (department != null) {
                idField.setText(String.valueOf(department.getId()));
                nameField.setText(department.getName());
                locationField.setText(department.getLocation());
                phoneField.setText(department.getPhone());
                statusLabel.setText("Department ID " + id + " found.");
                // Select in table if present
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(id)) {
                        departmentTable.setRowSelectionInterval(i, i);
                        departmentTable.scrollRectToVisible(departmentTable.getCellRect(i, 0, true));
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Department ID " + id + " not found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Department ID " + id + " not found.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDepartment() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a department from the table or search for one first.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || location.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, Location, and Phone are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Department department = new Department(id, name, location, phone);
            if (departmentDAO.updateDepartment(department)) {
                JOptionPane.showMessageDialog(this, "Department updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDepartmentTable();
                mainFrame.refreshDepartmentCombos();
                resetFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update department. It might not exist or a database error occurred.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID in the ID field. This should not happen if selected from table.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDepartment() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a department from the table to delete.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow == -1 && !idText.equals(tableModel.getValueAt(selectedRow,0).toString())){
             JOptionPane.showMessageDialog(this, "Please select a department from the table that matches the ID field for deletion.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            int confirmation = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete department ID " + id + "?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                if (departmentDAO.deleteDepartment(id)) {
                    JOptionPane.showMessageDialog(this, "Department deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshDepartmentTable();
                    mainFrame.refreshDepartmentCombos();
                    resetFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete department. It might be in use or a database error occurred.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID for deletion.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        idField.setText("");
        nameField.setText("");
        locationField.setText("");
        phoneField.setText("");
        statusLabel.setText(" ");
        departmentTable.clearSelection();
        idField.setEditable(false); // Keep ID field not directly editable
    }
} 
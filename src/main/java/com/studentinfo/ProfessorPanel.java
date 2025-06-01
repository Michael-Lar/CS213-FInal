package com.studentinfo;

import com.studentinfo.dao.ProfessorDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Professor management panel with professor ID, name, email, phone, and department assignment.
 * Handles adding, editing, and searching professor records using ProfessorDAO.
 */
class ProfessorPanel extends JPanel {
    private MainFrame mainFrame;
    private ProfessorDAO professorDAO;
    private JTextField idField, nameField, emailField, phoneField;
    private JComboBox<Department> departmentCombo;
    private JButton addButton, searchButton, editButton, resetButton, deleteButton;
    private JLabel statusLabel;
    private JTable professorTable;
    private DefaultTableModel tableModel;

    public ProfessorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.professorDAO = new ProfessorDAO(); // Initialize ProfessorDAO
        setupUI();
        refreshProfessorTable(); // Load data into table on setup
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10)); // Add some spacing

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Professor ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; idField = new JTextField(15); idField.setEditable(false); formPanel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; nameField = new JTextField(15); formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; emailField = new JTextField(15); formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; phoneField = new JTextField(15); formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; departmentCombo = new JComboBox<>(); updateDepartmentCombo(); formPanel.add(departmentCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; 
        statusLabel = new JLabel(" "); 
        formPanel.add(statusLabel, gbc);
        gbc.gridwidth = 1; // Reset gridwidth
        
        JPanel formFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formFieldsPanel.add(formPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add Professor");
        searchButton = new JButton("Search Professor (by ID)"); // Clarified search
        editButton = new JButton("Update Professor");
        deleteButton = new JButton("Delete Professor");
        resetButton = new JButton("Reset Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetButton);

        // Table panel
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Department ID"}; // Display dept_id for now
        tableModel = new DefaultTableModel(columnNames, 0);
        professorTable = new JTable(tableModel);
        professorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(professorTable);

        // Add components to main panel
        add(formFieldsPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> addProfessor());
        searchButton.addActionListener(e -> searchProfessor());
        editButton.addActionListener(e -> editProfessor());
        deleteButton.addActionListener(e -> deleteProfessor());
        resetButton.addActionListener(e -> resetFields());

        // Add table selection listener
        professorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && professorTable.getSelectedRow() != -1) {
                populateFieldsFromTable();
            }
        });
    }

    public void updateDepartmentCombo() {
        departmentCombo.removeAllItems();
        List<Department> departments = mainFrame.getDepartments(); // Fetches from DepartmentDAO via MainFrame
        if (departments != null) {
            for (Department dept : departments) {
                departmentCombo.addItem(dept);
            }
        }
    }

    private void addProfessor() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        Department selectedDepartment = (Department) departmentCombo.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || selectedDepartment == null) {
            JOptionPane.showMessageDialog(this, "All fields (Name, Email, Phone, Department) are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Prof ID is auto-incremented by DB, so we don't set it here
        Professor professor = new Professor(0, name, email, phone, selectedDepartment.getId()); 
        try {
            boolean success = professorDAO.addProfessor(professor);
            if (success) {
                JOptionPane.showMessageDialog(this, "Professor added successfully with ID: " + professor.getProf_id(), "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshProfessorTable();
                resetFields();
                mainFrame.refreshProfessorCombos(); // Refresh combos in other panels if necessary
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchProfessor() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Professor ID to search:"));
            try {
                Professor professor = professorDAO.getProfessorById(id);
                if (professor != null) {
                    populateFields(professor);
                    statusLabel.setText("Professor found.");
                } else {
                    JOptionPane.showMessageDialog(this, "Professor ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    statusLabel.setText("Professor not found.");
                }
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editProfessor() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            Department selectedDepartment = (Department) departmentCombo.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || selectedDepartment == null) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (idField.getText().trim().isEmpty()){
                 JOptionPane.showMessageDialog(this, "Please select a professor from the table or search to update.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            Professor professor = new Professor(id, name, email, phone, selectedDepartment.getId());
            try {
                boolean success = professorDAO.updateProfessor(professor);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Professor updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshProfessorTable();
                    resetFields();
                    mainFrame.refreshProfessorCombos();
                }
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format for update. Select a professor first.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProfessor() {
        try {
            if (idField.getText().trim().isEmpty()){
                 JOptionPane.showMessageDialog(this, "Please select a professor from the table or search to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            int id = Integer.parseInt(idField.getText().trim());
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete professor ID: " + id + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    boolean success = professorDAO.deleteProfessor(id);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Professor deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshProfessorTable();
                        resetFields();
                        mainFrame.refreshProfessorCombos();
                    }
                } catch (RuntimeException e) {
                    JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID for deletion. Select a professor first.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void refreshProfessorTable() {
        tableModel.setRowCount(0); // Clear existing data
        try {
            List<Professor> professors = professorDAO.getAllProfessors();
            if (professors != null) {
                for (Professor prof : professors) {
                    // For now, display dept_id. To display department name, a JOIN in SQL or a lookup is needed.
                    // Example: Department dept = mainFrame.findDepartmentById(prof.getDept_id());
                    // String deptName = (dept != null) ? dept.getName() : "Unknown";
                    Object[] rowData = {
                        prof.getProf_id(), 
                        prof.getProf_name(), 
                        prof.getEmail(), 
                        prof.getPhone(), 
                        prof.getDept_id() // or deptName if implementing the lookup
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Database error while refreshing table: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFields(Professor professor) {
        idField.setText(String.valueOf(professor.getProf_id()));
        nameField.setText(professor.getProf_name());
        emailField.setText(professor.getEmail());
        phoneField.setText(professor.getPhone());
        Department department = mainFrame.findDepartmentById(professor.getDept_id());
        if (department != null) {
            for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                if (departmentCombo.getItemAt(i).getId() == department.getId()) {
                    departmentCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        statusLabel.setText("Professor details loaded.");
    }

    private void populateFieldsFromTable() {
        int selectedRow = professorTable.getSelectedRow();
        if (selectedRow != -1) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            emailField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            phoneField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            int deptId = (int) tableModel.getValueAt(selectedRow, 4);
            
            Department department = mainFrame.findDepartmentById(deptId);
            if (department != null) {
                for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                    if (departmentCombo.getItemAt(i).getId() == department.getId()) {
                        departmentCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            statusLabel.setText("Selected Professor: " + nameField.getText());
        }
    }

    private void resetFields() {
        idField.setText(""); 
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        if (departmentCombo.getItemCount() > 0) {
            departmentCombo.setSelectedIndex(0);
        }
        statusLabel.setText(" ");
        professorTable.clearSelection();
        // idField should remain non-editable as it's auto-generated or set from table selection
    }
} 
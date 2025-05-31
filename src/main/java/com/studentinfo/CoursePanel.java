package com.studentinfo;

import javax.swing.*;
import java.awt.*;

/**
 * Course management panel with department-based instructor filtering.
 * Handles adding, editing, and searching course records.
 */
class CoursePanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField idField, nameField, creditsField;
    private JComboBox<Department> departmentCombo;
    private JComboBox<Instructor> instructorCombo;
    private JButton addButton, searchButton, editButton, resetButton;
    private JLabel statusLabel;

    public CoursePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Course ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Credits:"));
        creditsField = new JTextField();
        formPanel.add(creditsField);

        formPanel.add(new JLabel("Department:"));
        departmentCombo = new JComboBox<>();
        updateDepartmentCombo();
        departmentCombo.addActionListener(e -> updateInstructorCombo());
        formPanel.add(departmentCombo);

        formPanel.add(new JLabel("Instructor:"));
        instructorCombo = new JComboBox<>();
        updateInstructorCombo();
        formPanel.add(instructorCombo);

        statusLabel = new JLabel(" ");
        formPanel.add(statusLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add Course");
        searchButton = new JButton("Search");
        editButton = new JButton("Edit Course");
        resetButton = new JButton("Reset");

        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(editButton);
        buttonPanel.add(resetButton);

        // Add components to main panel
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> addCourse());
        searchButton.addActionListener(e -> searchCourse());
        editButton.addActionListener(e -> editCourse());
        resetButton.addActionListener(e -> resetFields());
    }

    public void updateDepartmentCombo() {
        departmentCombo.removeAllItems();
        for (int i = 0; i < mainFrame.getDepartments().size(); i++) {
            departmentCombo.addItem(mainFrame.getDepartments().get(i));
        }
    }

    /**
     * Updates instructor dropdown based on selected department.
     * Shows only instructors from the selected department.
     */
    public void updateInstructorCombo() {
        instructorCombo.removeAllItems();
        Department selectedDepartment = (Department) departmentCombo.getSelectedItem();
        if (selectedDepartment != null) {
            MyGenericList<Instructor> departmentInstructors = mainFrame.getInstructorsByDepartment(selectedDepartment.getId());
            for (int i = 0; i < departmentInstructors.size(); i++) {
                instructorCombo.addItem(departmentInstructors.get(i));
            }
        }
    }

    private void addCourse() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            int credits = Integer.parseInt(creditsField.getText().trim());
            Department selectedDepartment = (Department) departmentCombo.getSelectedItem();
            Instructor selectedInstructor = (Instructor) instructorCombo.getSelectedItem();

            // Validate fields
            if (name.isEmpty() || selectedDepartment == null || selectedInstructor == null) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if course ID already exists
            if (mainFrame.getCourses().size() > 0 && mainFrame.findCourseById(id) != null) {
                JOptionPane.showMessageDialog(this, "Course ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create new course and add to list
            Course course = new Course(id, name, credits, selectedDepartment.getId(), selectedInstructor.getId());
            mainFrame.getCourses().add(course);
            mainFrame.saveCourses();

            JOptionPane.showMessageDialog(this, "Course added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            resetFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID or credits format. Please enter numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCourse() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Course course = mainFrame.findCourseById(id);

            if (course != null) {
                idField.setText(String.valueOf(course.getId()));
                nameField.setText(course.getName());
                creditsField.setText(String.valueOf(course.getCredits()));
                
                // Set department in combo box
                Department department = mainFrame.findDepartmentById(course.getDepartmentId());
                if (department != null) {
                    for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                        if (departmentCombo.getItemAt(i).getId() == department.getId()) {
                            departmentCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
                
                // Set instructor in combo box
                Instructor instructor = mainFrame.findInstructorById(course.getInstructorId());
                if (instructor != null) {
                    for (int i = 0; i < instructorCombo.getItemCount(); i++) {
                        if (instructorCombo.getItemAt(i).getId() == instructor.getId()) {
                            instructorCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
                
                statusLabel.setText("Course found.");
            } else {
                JOptionPane.showMessageDialog(this, "Course ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editCourse() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Course course = mainFrame.findCourseById(id);

            if (course != null) {
                // Lock ID field
                idField.setEditable(false);
                
                // Update course information
                String name = nameField.getText().trim();
                int credits = Integer.parseInt(creditsField.getText().trim());
                Department selectedDepartment = (Department) departmentCombo.getSelectedItem();
                Instructor selectedInstructor = (Instructor) instructorCombo.getSelectedItem();

                // Validate fields
                if (name.isEmpty() || selectedDepartment == null || selectedInstructor == null) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                course.setName(name);
                course.setCredits(credits);
                course.setDepartmentId(selectedDepartment.getId());
                course.setInstructorId(selectedInstructor.getId());
                mainFrame.saveCourses();

                JOptionPane.showMessageDialog(this, "Course updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetFields();
            } else {
                JOptionPane.showMessageDialog(this, "Course ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID or credits format. Please enter numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        idField.setText("");
        nameField.setText("");
        creditsField.setText("");
        departmentCombo.setSelectedIndex(0);
        instructorCombo.setSelectedIndex(0);
        statusLabel.setText(" ");
        
        // Ensure ID field is editable and has normal background color
        idField.setEditable(true);
        idField.setBackground(UIManager.getColor("TextField.background"));
    }
} 
package com.studentinfo;

import javax.swing.*;
import java.awt.*;

/**
 * Enrollment management panel for handling student course enrollments.
 * Provides functionality to add, search, and manage enrollments using database operations.
 */
class EnrollmentPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField studentIdField, courseIdField;
    private JComboBox<String> yearCombo, semesterCombo;
    private JButton addButton, searchButton, resetButton;
    private JLabel statusLabel;
    
    private String[] years = {"2023", "2024", "2025"};
    private String[] semesters = {"Fall", "Spring", "Summer"};

    public EnrollmentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        formPanel.add(studentIdField);

        formPanel.add(new JLabel("Course ID (DB ID):"));
        courseIdField = new JTextField();
        formPanel.add(courseIdField);

        formPanel.add(new JLabel("Year:"));
        yearCombo = new JComboBox<>(years);
        formPanel.add(yearCombo);

        formPanel.add(new JLabel("Semester:"));
        semesterCombo = new JComboBox<>(semesters);
        formPanel.add(semesterCombo);

        statusLabel = new JLabel(" ");
        formPanel.add(statusLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add Enrollment");
        searchButton = new JButton("Search Enrollment");
        resetButton = new JButton("Reset Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(resetButton);

        // Add components to main panel
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> addEnrollment());
        searchButton.addActionListener(e -> searchEnrollment());
        resetButton.addActionListener(e -> resetFields());
    }

    private void addEnrollment() {
        try {
            String studentIdText = studentIdField.getText().trim();
            String courseIdText = courseIdField.getText().trim();
            if (studentIdText.isEmpty() || courseIdText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Student ID and Course ID are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int studentId = Integer.parseInt(studentIdText);
            int courseId = Integer.parseInt(courseIdText);
            String year = yearCombo.getSelectedItem().toString();
            String semester = semesterCombo.getSelectedItem().toString();

            // Validate student exists
            Student student = mainFrame.findStudentById(studentId);
            if (student == null) {
                JOptionPane.showMessageDialog(this, "Student ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate course exists (by its auto-incremented course_id)
            Course course = mainFrame.findCourseById(courseId);
            if (course == null) {
                JOptionPane.showMessageDialog(this, "Course (DB) ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check for duplicate enrollment using DAO via MainFrame
            if (mainFrame.findEnrollmentInDB(studentId, courseId, year, semester) != null) {
                JOptionPane.showMessageDialog(this, 
                    "Student is already enrolled in this course for the selected semester.", 
                    "Duplicate Enrollment", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create new enrollment object (enrollment_id will be set by DAO)
            Enrollment newEnrollment = new Enrollment(0, studentId, courseId, year, semester, null);
            
            if (mainFrame.addEnrollmentToDB(newEnrollment)) {
                JOptionPane.showMessageDialog(this, "Enrollment added successfully. Enrollment ID: " + newEnrollment.getEnrollmentId(), "Success", JOptionPane.INFORMATION_MESSAGE);
                resetFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add enrollment to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter numbers for Student ID and Course ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchEnrollment() {
        try {
            String studentIdText = studentIdField.getText().trim();
            String courseIdText = courseIdField.getText().trim();
            if (studentIdText.isEmpty() || courseIdText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Student ID and Course ID are required to search.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int studentId = Integer.parseInt(studentIdText);
            int courseId = Integer.parseInt(courseIdText);
            String year = yearCombo.getSelectedItem().toString();
            String semester = semesterCombo.getSelectedItem().toString();

            Enrollment enrollment = mainFrame.findEnrollmentInDB(studentId, courseId, year, semester);

            if (enrollment != null) {
                statusLabel.setText("Enrollment found. Grade: " + (enrollment.getGrade() != null ? enrollment.getGrade() : "Not Graded"));
            } else {
                statusLabel.setText("Enrollment not found.");
                JOptionPane.showMessageDialog(this, "Enrollment not found for the specified details.", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter numbers for Student ID and Course ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        studentIdField.setText("");
        courseIdField.setText("");
        if (yearCombo.getItemCount() > 0) yearCombo.setSelectedIndex(0);
        if (semesterCombo.getItemCount() > 0) semesterCombo.setSelectedIndex(0);
        statusLabel.setText(" ");
        studentIdField.requestFocus();
    }
} 
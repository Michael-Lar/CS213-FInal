package com.studentinfo;

import javax.swing.*;
import java.awt.*;

/**
 * Enrollment management panel for handling student course enrollments.
 * Provides functionality to add, search, and manage enrollments.
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

        formPanel.add(new JLabel("Course ID:"));
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
        searchButton = new JButton("Search");
        resetButton = new JButton("Reset");

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
            int studentId = Integer.parseInt(studentIdField.getText().trim());
            int courseId = Integer.parseInt(courseIdField.getText().trim());
            String year = yearCombo.getSelectedItem().toString();
            String semester = semesterCombo.getSelectedItem().toString();

            // Validate student exists
            Student student = mainFrame.findStudentById(studentId);
            if (student == null) {
                JOptionPane.showMessageDialog(this, "Student ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate course exists
            Course course = mainFrame.findCourseById(courseId);
            if (course == null) {
                JOptionPane.showMessageDialog(this, "Course ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check for duplicate enrollment
            for (Enrollment enrollment : mainFrame.getEnrollments()) {
                if (enrollment.getStudentId() == studentId && 
                    enrollment.getCourseId() == courseId &&
                    enrollment.getYear().equals(year) &&
                    enrollment.getSemester().equals(semester)) {
                    JOptionPane.showMessageDialog(this, 
                        "Student is already enrolled in this course for the selected semester.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Create new enrollment
            Enrollment enrollment = new Enrollment(studentId, courseId, year, semester);
            mainFrame.getEnrollments().add(enrollment);
            mainFrame.saveEnrollments();

            JOptionPane.showMessageDialog(this, "Enrollment added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            resetFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchEnrollment() {
        try {
            int studentId = Integer.parseInt(studentIdField.getText().trim());
            int courseId = Integer.parseInt(courseIdField.getText().trim());
            String year = yearCombo.getSelectedItem().toString();
            String semester = semesterCombo.getSelectedItem().toString();

            boolean found = false;
            for (Enrollment enrollment : mainFrame.getEnrollments()) {
                if (enrollment.getStudentId() == studentId && 
                    enrollment.getCourseId() == courseId &&
                    enrollment.getYear().equals(year) &&
                    enrollment.getSemester().equals(semester)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                statusLabel.setText("Enrollment found.");
            } else {
                JOptionPane.showMessageDialog(this, "Enrollment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        studentIdField.setText("");
        courseIdField.setText("");
        yearCombo.setSelectedIndex(0);
        semesterCombo.setSelectedIndex(0);
        statusLabel.setText(" ");
    }
} 
package com.studentinfo;

import javax.swing.*;
import java.awt.*;


/**
 * Report generation panel for course enrollment and grade reports.
 * Provides formatted output with student and grade information.
 */
class ReportPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField courseIdField;
    private JComboBox<String> yearCombo, semesterCombo;
    private JButton generateReportButton;
    private JTextArea reportArea;
    
    private String[] years = {"2023", "2024", "2025"};
    private String[] semesters = {"Fall", "Spring", "Summer"};

    public ReportPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Course ID:"));
        courseIdField = new JTextField(10);
        formPanel.add(courseIdField);
        
        formPanel.add(new JLabel("Year:"));
        yearCombo = new JComboBox<>(years);
        formPanel.add(yearCombo);
        
        formPanel.add(new JLabel("Semester:"));
        semesterCombo = new JComboBox<>(semesters);
        formPanel.add(semesterCombo);
        
        generateReportButton = new JButton("Generate Report");
        formPanel.add(generateReportButton);

        // Report area
        reportArea = new JTextArea(20, 50);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);

        // Add components to main panel
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        generateReportButton.addActionListener(e -> generateReport());
    }

    /**
     * Generates detailed course report including enrolled students and grades.
     * Uses monospaced font for aligned output.
     */
    private void generateReport() {
        try {
            int courseId = Integer.parseInt(courseIdField.getText().trim());
            String year = yearCombo.getSelectedItem().toString();
            String semester = semesterCombo.getSelectedItem().toString();
            
            Course course = mainFrame.findCourseById(courseId);
            if (course == null) {
                JOptionPane.showMessageDialog(this, "Course ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            StringBuilder report = new StringBuilder();
            report.append("Course Report\n");
            report.append("=============\n\n");
            report.append("Course ID: ").append(courseId).append("\n");
            report.append("Course Name: ").append(course.getName()).append("\n");
            report.append("Department ID: ").append(course.getDepartmentId()).append("\n");
            report.append("Credits: ").append(course.getCredits()).append("\n");
            report.append("Year: ").append(year).append("\n");
            report.append("Semester: ").append(semester).append("\n\n");
            report.append("Enrolled Students:\n");
            report.append("==================\n\n");
            
            // Use monospaced font formatting for better alignment
            Font originalFont = reportArea.getFont();
            Font monoFont = new Font(Font.MONOSPACED, Font.PLAIN, originalFont.getSize());
            reportArea.setFont(monoFont);
            
            // Fixed width columns
            report.append(String.format("%-10s %-30s %-10s\n", "Student ID", "Name", "Grade"));
            report.append(String.format("%-10s %-30s %-10s\n", "----------", "------------------------------", "----------"));
            
            boolean studentsFound = false;
            for (Enrollment enrollment : mainFrame.getEnrollments()) {
                if (enrollment.getCourseId() == courseId && 
                    enrollment.getYear().equals(year) && 
                    enrollment.getSemester().equals(semester)) {
                    
                    Student student = mainFrame.findStudentById(enrollment.getStudentId());
                    if (student != null) {
                        report.append(String.format("%-10d %-30s %-10s\n", 
                                    student.getId(), 
                                    student.getName(), 
                                    enrollment.getGrade() != null ? enrollment.getGrade() : "Not Graded"));
                        studentsFound = true;
                    }
                }
            }
            
            if (!studentsFound) {
                report.append("No students enrolled in this course for the selected semester.\n");
            }
            
            reportArea.setText(report.toString());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Course ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 
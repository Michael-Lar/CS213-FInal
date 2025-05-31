package com.studentinfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


/**
 * Grade management panel with course/student based filtering.
 * Handles grade assignment and updates.
 */
class GradePanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField studentIdField, courseIdField;
    private JComboBox<String> yearCombo, semesterCombo, gradeCombo;
    private JButton searchByStudentButton, searchByCourseButton, updateGradeButton;
    private JTable gradesTable;
    private DefaultTableModel tableModel;
    
    private String[] years = {"2023", "2024", "2025"};
    private String[] semesters = {"Fall", "Spring", "Summer"};
    private String[] grades = {"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F"};

    public GradePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Main top panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel - contains student and course search sections
        JPanel searchPanel = new JPanel(new GridLayout(2, 1, 5, 10));

        // Student search section
        JPanel studentSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        studentSearchPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField(10);
        studentSearchPanel.add(studentIdField);
        searchByStudentButton = new JButton("Search by Student");
        studentSearchPanel.add(searchByStudentButton);
        searchPanel.add(studentSearchPanel);

        // Course search section
        JPanel courseSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        courseSearchPanel.add(new JLabel("Course ID:"));
        courseIdField = new JTextField(10);
        courseSearchPanel.add(courseIdField);
        searchByCourseButton = new JButton("Search by Course");
        courseSearchPanel.add(searchByCourseButton);
        searchPanel.add(courseSearchPanel);

        topPanel.add(searchPanel);
        
        // Add some spacing between sections
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Grade section - for updating grades
        JPanel gradePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        gradePanel.setBorder(BorderFactory.createTitledBorder("Update Grade"));
        
        gradePanel.add(new JLabel("Year:"));
        yearCombo = new JComboBox<>(years);
        yearCombo.setPreferredSize(new Dimension(80, 25));
        gradePanel.add(yearCombo);
        
        gradePanel.add(new JLabel("Semester:"));
        semesterCombo = new JComboBox<>(semesters);
        semesterCombo.setPreferredSize(new Dimension(100, 25));
        gradePanel.add(semesterCombo);
        
        gradePanel.add(new JLabel("Grade:"));
        gradeCombo = new JComboBox<>(grades);
        gradeCombo.setPreferredSize(new Dimension(60, 25));
        gradePanel.add(gradeCombo);
        
        updateGradeButton = new JButton("Update Grade");
        gradePanel.add(updateGradeButton);
        
        topPanel.add(gradePanel);

        // Table for viewing grades
        String[] columnNames = {"Student ID", "Student Name", "Course ID", "Course Name", "Year", "Semester", "Grade"};
        tableModel = new DefaultTableModel(columnNames, 0);
        gradesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(gradesTable);

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        searchByStudentButton.addActionListener(e -> searchByStudent());
        searchByCourseButton.addActionListener(e -> searchByCourse());
        updateGradeButton.addActionListener(e -> updateGrade());
    }

    private void searchByStudent() {
        tableModel.setRowCount(0); // Clear table

        try {
            int studentId = Integer.parseInt(studentIdField.getText().trim());
            Student student = mainFrame.findStudentById(studentId);

            if (student == null) {
                JOptionPane.showMessageDialog(this, "Student ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (Enrollment enrollment : mainFrame.getEnrollments()) {
                if (enrollment.getStudentId() == studentId) {
                    Course course = mainFrame.findCourseById(enrollment.getCourseId());
                    if (course != null) {
                        Object[] row = {
                            enrollment.getStudentId(),
                            student.getName(),
                            enrollment.getCourseId(),
                            course.getName(),
                            enrollment.getYear(),
                            enrollment.getSemester(),
                            enrollment.getGrade() != null ? enrollment.getGrade() : "Not Graded"
                        };
                        tableModel.addRow(row);
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchByCourse() {
        tableModel.setRowCount(0); // Clear table

        try {
            int courseId = Integer.parseInt(courseIdField.getText().trim());
            Course course = mainFrame.findCourseById(courseId);

            if (course == null) {
                JOptionPane.showMessageDialog(this, "Course ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (Enrollment enrollment : mainFrame.getEnrollments()) {
                if (enrollment.getCourseId() == courseId) {
                    Student student = mainFrame.findStudentById(enrollment.getStudentId());
                    if (student != null) {
                        Object[] row = {
                            enrollment.getStudentId(),
                            student.getName(),
                            enrollment.getCourseId(),
                            course.getName(),
                            enrollment.getYear(),
                            enrollment.getSemester(),
                            enrollment.getGrade() != null ? enrollment.getGrade() : "Not Graded"
                        };
                        tableModel.addRow(row);
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates grade for student/course enrollment.
     * Supports updating by either student ID or course ID.
     */
    private void updateGrade() {
        try {
            // Get student ID if provided
            String studentIdText = studentIdField.getText().trim();
            int studentId = -1;
            if (!studentIdText.isEmpty()) {
                studentId = Integer.parseInt(studentIdText);
            }
            
            // Get course ID if provided
            String courseIdText = courseIdField.getText().trim();
            int courseId = -1;
            if (!courseIdText.isEmpty()) {
                courseId = Integer.parseInt(courseIdText);
            }
            
            String year = yearCombo.getSelectedItem().toString();
            String semester = semesterCombo.getSelectedItem().toString();
            String grade = gradeCombo.getSelectedItem().toString();
            
            // Validate that we have either student ID or course ID
            if (studentId <= 0 && courseId <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter either a Student ID or Course ID.", 
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            boolean found = false;
            // Update grade for selected student/course combination
            for (Enrollment enrollment : mainFrame.getEnrollments()) {
                boolean matchesStudent = (studentId > 0) ? enrollment.getStudentId() == studentId : true;
                boolean matchesCourse = (courseId > 0) ? enrollment.getCourseId() == courseId : true;
                
                if (matchesStudent && matchesCourse &&
                    enrollment.getYear().equals(year) &&
                    enrollment.getSemester().equals(semester)) {
                    
                    enrollment.setGrade(grade);
                    found = true;
                }
            }

            if (found) {
                mainFrame.saveEnrollments();
                JOptionPane.showMessageDialog(this, "Grade updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Refresh the table based on the current search mode
                if (studentId > 0) {
                    searchByStudent();
                } else if (courseId > 0) {
                    searchByCourse();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No enrollment found for the specified " + 
                    (studentId > 0 ? "student" : "course") + 
                    " in " + semester + " " + year, 
                    "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter numbers.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 
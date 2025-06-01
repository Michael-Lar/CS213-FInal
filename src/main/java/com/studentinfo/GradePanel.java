package com.studentinfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Grade management panel with course/student based filtering using DAO.
 * Handles grade assignment and updates.
 */
class GradePanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField studentIdField, courseIdField;
    private JTextField updateStudentIdField, updateCourseIdField;
    private JComboBox<String> yearCombo, semesterCombo, gradeCombo;
    private JButton searchByStudentButton, searchByCourseButton, updateGradeButton;
    private JTable gradesTable;
    private DefaultTableModel tableModel;
    
    private String[] years = {"2023", "2024", "2025"};
    private String[] semesters = {"Fall", "Spring", "Summer"};
    private String[] grades = {"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F", ""};

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

        // Search panel
        JPanel searchInputPanel = new JPanel(new GridLayout(2, 1, 5, 10));

        JPanel studentSearchLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        studentSearchLine.add(new JLabel("Search by Student ID:"));
        studentIdField = new JTextField(10);
        studentSearchLine.add(studentIdField);
        searchByStudentButton = new JButton("Get Student Grades");
        studentSearchLine.add(searchByStudentButton);
        searchInputPanel.add(studentSearchLine);

        JPanel courseSearchLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        courseSearchLine.add(new JLabel("Search by Course ID:"));
        courseIdField = new JTextField(10);
        courseSearchLine.add(courseIdField);
        searchByCourseButton = new JButton("Get Course Enrollments");
        courseSearchLine.add(searchByCourseButton);
        searchInputPanel.add(courseSearchLine);
        topPanel.add(searchInputPanel);
        
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Grade update section
        JPanel gradeUpdatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        gradeUpdatePanel.setBorder(BorderFactory.createTitledBorder("Update Grade for Specific Enrollment"));
        
        gradeUpdatePanel.add(new JLabel("Student ID:"));
        updateStudentIdField = new JTextField(8);
        gradeUpdatePanel.add(updateStudentIdField);

        gradeUpdatePanel.add(new JLabel("Course ID:"));
        updateCourseIdField = new JTextField(8);
        gradeUpdatePanel.add(updateCourseIdField);

        gradeUpdatePanel.add(new JLabel("Year:"));
        yearCombo = new JComboBox<>(years);
        yearCombo.setPreferredSize(new Dimension(80, 25));
        gradeUpdatePanel.add(yearCombo);
        
        gradeUpdatePanel.add(new JLabel("Semester:"));
        semesterCombo = new JComboBox<>(semesters);
        semesterCombo.setPreferredSize(new Dimension(100, 25));
        gradeUpdatePanel.add(semesterCombo);
        
        gradeUpdatePanel.add(new JLabel("New Grade:"));
        gradeCombo = new JComboBox<>(grades);
        gradeCombo.setPreferredSize(new Dimension(70, 25));
        gradeUpdatePanel.add(gradeCombo);
        
        updateGradeButton = new JButton("Set Grade");
        gradeUpdatePanel.add(updateGradeButton);
        topPanel.add(gradeUpdatePanel);

        // Table for viewing grades
        String[] columnNames = {"Enroll ID", "Std ID", "Student Name", "Crs ID", "Course Name", "Year", "Semester", "Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gradesTable = new JTable(tableModel);
        gradesTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && gradesTable.getSelectedRow() != -1) {
                int selectedRow = gradesTable.getSelectedRow();
                updateStudentIdField.setText(gradesTable.getValueAt(selectedRow, 1).toString());
                updateCourseIdField.setText(gradesTable.getValueAt(selectedRow, 3).toString());
                yearCombo.setSelectedItem(gradesTable.getValueAt(selectedRow, 5).toString());
                semesterCombo.setSelectedItem(gradesTable.getValueAt(selectedRow, 6).toString());
                String currentGrade = gradesTable.getValueAt(selectedRow, 7) != null ? gradesTable.getValueAt(selectedRow, 7).toString() : "";
                gradeCombo.setSelectedItem(currentGrade);
            }
        });
        JScrollPane scrollPane = new JScrollPane(gradesTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchByStudentButton.addActionListener(e -> searchByStudent());
        searchByCourseButton.addActionListener(e -> searchByCourse());
        updateGradeButton.addActionListener(e -> updateGrade());
    }

    private void searchByStudent() {
        tableModel.setRowCount(0); 
        String studentIdText = studentIdField.getText().trim();
        if (studentIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID to search.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            int studentId = Integer.parseInt(studentIdText);
            Student student = mainFrame.findStudentById(studentId);
            if (student == null) {
                JOptionPane.showMessageDialog(this, "Student ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Enrollment> enrollments = mainFrame.getEnrollmentsForStudentFromDB(studentId);
            if (enrollments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No enrollments found for this student.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (Enrollment enrollment : enrollments) {
                Course course = mainFrame.findCourseById(enrollment.getCourseId());
                String courseName = (course != null) ? course.getCourseName() : "N/A";
                tableModel.addRow(new Object[]{
                    enrollment.getEnrollmentId(),
                    enrollment.getStudentId(),
                    student.getName(),
                    enrollment.getCourseId(),
                    courseName,
                    enrollment.getYear(),
                    enrollment.getSemester(),
                    enrollment.getGrade() != null ? enrollment.getGrade() : ""
                });
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Student ID format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchByCourse() {
        tableModel.setRowCount(0);
        String courseIdText = courseIdField.getText().trim();
        if (courseIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Course ID to search.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            int courseId = Integer.parseInt(courseIdText);
            Course course = mainFrame.findCourseById(courseId);
            if (course == null) {
                JOptionPane.showMessageDialog(this, "Course ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Enrollment> enrollments = mainFrame.getEnrollmentsForCourseFromDB(courseId);
            if (enrollments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No enrollments found for this course.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (Enrollment enrollment : enrollments) {
                Student student = mainFrame.findStudentById(enrollment.getStudentId());
                String studentName = (student != null) ? student.getName() : "N/A";
                tableModel.addRow(new Object[]{
                    enrollment.getEnrollmentId(),
                    enrollment.getStudentId(),
                    studentName,
                    enrollment.getCourseId(),
                    course.getCourseName(),
                    enrollment.getYear(),
                    enrollment.getSemester(),
                    enrollment.getGrade() != null ? enrollment.getGrade() : ""
                });
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Course ID format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateGrade() {
        String studentIdText = updateStudentIdField.getText().trim();
        String courseIdText = updateCourseIdField.getText().trim();
        
        if (studentIdText.isEmpty() || courseIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID and Course ID are required in the 'Update Grade' section. Select a row from the table or enter them manually.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdText);
            int courseId = Integer.parseInt(courseIdText);
            String year = yearCombo.getSelectedItem().toString();
            String semester = semesterCombo.getSelectedItem().toString();
            String grade = gradeCombo.getSelectedItem() != null ? gradeCombo.getSelectedItem().toString() : null;
            if (grade != null && grade.isEmpty()) grade = null;

            if (mainFrame.updateGradeInDB(studentId, courseId, year, semester, grade)) {
                JOptionPane.showMessageDialog(this, "Grade updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (!studentIdField.getText().trim().isEmpty()) {
                    searchByStudent(); 
                } else if (!courseIdField.getText().trim().isEmpty()) {
                    searchByCourse();
                } else { 
                    tableModel.setRowCount(0);
                }
                updateStudentIdField.setText("");
                updateCourseIdField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update grade. Ensure the enrollment exists for the specified student, course, year, and semester.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Student ID or Course ID in 'Update Grade' section.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 
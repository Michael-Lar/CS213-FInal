package com.studentinfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import com.studentinfo.dao.CourseDAO;

/**
 * Course management panel with department-based professor filtering.
 * Handles adding, editing, and searching course records using DAO.
 */
class CoursePanel extends JPanel {
    private MainFrame mainFrame;
    private CourseDAO courseDAO;
    private JTextField courseNumberField;
    private JTextField courseNameField;
    private JTextField creditsField;
    private JComboBox<Department> departmentCombo;
    private JComboBox<Professor> professorCombo;
    private JButton addButton, searchButton, editButton, resetButton;
    private JLabel statusLabel;
    private int currentCourseId = -1;

    public CoursePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.courseDAO = new CourseDAO();
        setupUI();
        resetFields();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Course Number:"));
        courseNumberField = new JTextField();
        formPanel.add(courseNumberField);

        formPanel.add(new JLabel("Course Name:"));
        courseNameField = new JTextField();
        formPanel.add(courseNameField);

        formPanel.add(new JLabel("Credits:"));
        creditsField = new JTextField();
        formPanel.add(creditsField);

        formPanel.add(new JLabel("Department:"));
        departmentCombo = new JComboBox<>();
        formPanel.add(departmentCombo);

        formPanel.add(new JLabel("Professor:"));
        professorCombo = new JComboBox<>();
        formPanel.add(professorCombo);

        statusLabel = new JLabel(" ");
        formPanel.add(statusLabel);

        // Initialize combos after all components are created
        updateDepartmentCombo();
        departmentCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfessorCombo();
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add Course");
        searchButton = new JButton("Search by Number");
        editButton = new JButton("Update Course");
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
        searchButton.addActionListener(e -> searchCourseByNumber());
        editButton.addActionListener(e -> editCourse());
        resetButton.addActionListener(e -> resetFields());
    }

    public void updateDepartmentCombo() {
        Department selectedItem = (Department) departmentCombo.getSelectedItem();
        departmentCombo.removeAllItems();
        List<Department> departments = mainFrame.getDepartments();
        if (departments != null) {
            for (Department dept : departments) {
                departmentCombo.addItem(dept);
            }
        }
        if (selectedItem != null) {
            for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                if (departmentCombo.getItemAt(i).getId() == selectedItem.getId()) {
                    departmentCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        updateProfessorCombo();
    }

    public void updateProfessorCombo() {
        Professor selectedProf = (Professor) professorCombo.getSelectedItem();
        professorCombo.removeAllItems();
        Department selectedDepartment = (Department) departmentCombo.getSelectedItem();
        if (selectedDepartment != null) {
            List<Professor> professors = mainFrame.getProfessorsByDepartment(selectedDepartment.getId());
            if (professors != null) {
                for (Professor prof : professors) {
                    professorCombo.addItem(prof);
                }
            }
            if (selectedProf != null && selectedDepartment.getId() == selectedProf.getDept_id()) {
                for (int i = 0; i < professorCombo.getItemCount(); i++) {
                    if (professorCombo.getItemAt(i).getProf_id() == selectedProf.getProf_id()) {
                        professorCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void addCourse() {
        String courseNumber = courseNumberField.getText().trim();
        String courseName = courseNameField.getText().trim();
        String creditsText = creditsField.getText().trim();
        Department selectedDepartment = (Department) departmentCombo.getSelectedItem();
        Professor selectedProfessor = (Professor) professorCombo.getSelectedItem();

        if (courseNumber.isEmpty() || courseName.isEmpty() || creditsText.isEmpty() || selectedDepartment == null) {
            JOptionPane.showMessageDialog(this, "Course Number, Name, Credits, and Department are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedProfessor == null && professorCombo.getItemCount() > 0) {
            JOptionPane.showMessageDialog(this, "Please select a Professor or ensure one exists for the department.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(creditsText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid format for Credits. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (courseDAO.getCourseByCourseNumber(courseNumber) != null) {
            JOptionPane.showMessageDialog(this, "Course Number already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Course newCourse = new Course(0, courseNumber, courseName, credits, selectedDepartment.getId(), selectedProfessor != null ? selectedProfessor.getProf_id() : 0);

        if (courseDAO.addCourse(newCourse)) {
            currentCourseId = newCourse.getId();
            JOptionPane.showMessageDialog(this, "Course added successfully. DB ID: " + currentCourseId, "Success", JOptionPane.INFORMATION_MESSAGE);
            resetFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add course.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCourseByNumber() {
        String courseNumber = courseNumberField.getText().trim();
        if (courseNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Course Number to search.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Course course = courseDAO.getCourseByCourseNumber(courseNumber);

        if (course != null) {
            populateFields(course);
            statusLabel.setText("Course found: " + course.getCourseNumber());
            courseNumberField.setEditable(false);
            addButton.setEnabled(false);
            editButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Course Number not found.", "Error", JOptionPane.ERROR_MESSAGE);
            resetFields();
        }
    }

    private void populateFields(Course course) {
        currentCourseId = course.getId();
        courseNumberField.setText(course.getCourseNumber());
        courseNameField.setText(course.getCourseName());
        creditsField.setText(String.valueOf(course.getCredits()));

        Department courseDept = mainFrame.findDepartmentById(course.getDepartmentId());
        if (courseDept != null) {
            for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                if (departmentCombo.getItemAt(i).getId() == courseDept.getId()) {
                    departmentCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        Professor courseProf = mainFrame.findProfessorById(course.getProfessorId());
        if (courseProf != null) {
            for (int i = 0; i < professorCombo.getItemCount(); i++) {
                if (professorCombo.getItemAt(i) != null && professorCombo.getItemAt(i).getProf_id() == course.getProfessorId()) {
                    professorCombo.setSelectedIndex(i);
                    break;
                }
            }
        } else if (course.getProfessorId() == 0 || course.getProfessorId() == -1) {
            professorCombo.setSelectedIndex(-1);
        }
    }

    private void editCourse() {
        if (currentCourseId == -1) {
            JOptionPane.showMessageDialog(this, "Please search for a course to update first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String courseNumber = courseNumberField.getText().trim();
        String courseName = courseNameField.getText().trim();
        String creditsText = creditsField.getText().trim();
        Department selectedDepartment = (Department) departmentCombo.getSelectedItem();
        Professor selectedProfessor = (Professor) professorCombo.getSelectedItem();

        if (courseNumber.isEmpty() || courseName.isEmpty() || creditsText.isEmpty() || selectedDepartment == null) {
            JOptionPane.showMessageDialog(this, "Course Number, Name, Credits, and Department are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedProfessor == null && professorCombo.getItemCount() > 0) {
            JOptionPane.showMessageDialog(this, "Please select a Professor.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(creditsText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid format for Credits. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Course existingCourseWithSameNumber = courseDAO.getCourseByCourseNumber(courseNumber);
        if (existingCourseWithSameNumber != null && existingCourseWithSameNumber.getId() != currentCourseId) {
            JOptionPane.showMessageDialog(this, "Another course with this Course Number already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Course courseToUpdate = new Course(currentCourseId, courseNumber, courseName, credits, selectedDepartment.getId(), selectedProfessor != null ? selectedProfessor.getProf_id() : 0);

        if (courseDAO.updateCourse(courseToUpdate)) {
            JOptionPane.showMessageDialog(this, "Course updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            resetFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update course.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        currentCourseId = -1;
        courseNumberField.setText("");
        courseNameField.setText("");
        creditsField.setText("");
        
        if (departmentCombo.getItemCount() > 0) {
            departmentCombo.setSelectedIndex(0);
        } else {
            departmentCombo.removeAllItems();
            updateProfessorCombo();
        }

        if (departmentCombo.getItemCount() == 0 && professorCombo.getItemCount() > 0) {
            professorCombo.setSelectedIndex(-1);
        }

        statusLabel.setText(" ");
        courseNumberField.setEditable(true);
        courseNumberField.requestFocus();
        addButton.setEnabled(true);
        editButton.setEnabled(false);
    }
} 
package com.studentinfo;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Main application window containing all panels and data management.
 * Handles data persistence and provides access to shared data stores.
 */
class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    
    // Shared data stores using generic linked list
    private MyGenericList<Student> students;
    private MyGenericList<Course> courses;
    private MyGenericList<Department> departments;
    private MyGenericList<Instructor> instructors;
    private List<Enrollment> enrollments;
    
    // File paths
    private static final String STUDENT_FILE = "data/students.dat";
    private static final String COURSE_FILE = "data/courses.dat";
    private static final String DEPARTMENT_FILE = "data/departments.dat";
    private static final String INSTRUCTOR_FILE = "data/instructors.dat";
    private static final String ENROLLMENT_FILE = "data/enrollments.dat";
    
    // Panels
    private StudentPanel studentPanel;
    private CoursePanel coursePanel;
    private DepartmentPanel departmentPanel;
    private InstructorPanel instructorPanel;
    private EnrollmentPanel enrollmentPanel;
    private GradePanel gradePanel;
    private ReportPanel reportPanel;

    public MainFrame() {
        setTitle("Student Information System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize data stores
        students = new MyGenericList<>();
        courses = new MyGenericList<>();
        departments = new MyGenericList<>();
        instructors = new MyGenericList<>();
        enrollments = new ArrayList<>();
        
        // Load data
        loadStudents();
        loadCourses();
        loadDepartments();
        loadInstructors();
        loadEnrollments();
        
        // Initialize panels with shared data
        tabbedPane = new JTabbedPane();
        studentPanel = new StudentPanel(this);
        coursePanel = new CoursePanel(this);
        departmentPanel = new DepartmentPanel(this);
        instructorPanel = new InstructorPanel(this);
        enrollmentPanel = new EnrollmentPanel(this);
        gradePanel = new GradePanel(this);
        reportPanel = new ReportPanel(this);

        tabbedPane.addTab("Students", studentPanel);
        tabbedPane.addTab("Courses", coursePanel);
        tabbedPane.addTab("Departments", departmentPanel);
        tabbedPane.addTab("Instructors", instructorPanel);
        tabbedPane.addTab("Enrollments", enrollmentPanel);
        tabbedPane.addTab("Grades", gradePanel);
        tabbedPane.addTab("Reports", reportPanel);

        add(tabbedPane);
    }
    
    // Data access methods
    public MyGenericList<Student> getStudents() {
        return students;
    }
    
    public MyGenericList<Course> getCourses() {
        return courses;
    }
    
    public MyGenericList<Department> getDepartments() {
        return departments;
    }
    
    public MyGenericList<Instructor> getInstructors() {
        return instructors;
    }
    
    public List<Enrollment> getEnrollments() {
        return enrollments;
    }
    
    // Method to refresh all department combos
    public void refreshDepartmentCombos() {
        if (coursePanel != null) {
            coursePanel.updateDepartmentCombo();
            coursePanel.updateInstructorCombo();
        }
        if (instructorPanel != null) {
            instructorPanel.updateDepartmentCombo();
        }
    }
    
    // Add new method for instructor updates
    public void refreshInstructorCombos() {
        if (coursePanel != null) {
            coursePanel.updateInstructorCombo();
        }
    }
    
    /**
     * Saves an object to file with error handling.
     * @param obj Object to save
     * @param filePath Path to save the file
     * @param errorPrefix Prefix for error messages
     */
    private void saveObject(Object obj, String filePath, String errorPrefix) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                errorPrefix + ": " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads a MyGenericList from file with error handling.
     * @param filePath Path to load the file
     * @param errorPrefix Prefix for error messages
     * @return Loaded list or new empty list if loading fails
     */
    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> MyGenericList<T> loadList(String filePath, String errorPrefix) {
        File file = new File(filePath);
        MyGenericList<T> list = new MyGenericList<>();
        
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object loadedData = ois.readObject();
                if (loadedData instanceof MyGenericList) {
                    list = (MyGenericList<T>) loadedData;
                } else {
                    System.out.println("Warning: " + errorPrefix + " data format mismatch. Starting with empty list.");
                }
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading " + errorPrefix + ": " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        return list;
    }

    // Updated save methods using utility method
    public void saveStudents() {
        saveObject(students, STUDENT_FILE, "Error saving students");
    }
    
    public void saveCourses() {
        saveObject(courses, COURSE_FILE, "Error saving courses");
    }
    
    public void saveDepartments() {
        saveObject(departments, DEPARTMENT_FILE, "Error saving departments");
    }
    
    public void saveInstructors() {
        saveObject(instructors, INSTRUCTOR_FILE, "Error saving instructors");
    }
    
    public void saveEnrollments() {
        saveObject(enrollments, ENROLLMENT_FILE, "Error saving enrollments");
    }
    
    // Updated load methods using utility method
    private void loadStudents() {
        students = loadList(STUDENT_FILE, "students");
    }
    
    private void loadCourses() {
        courses = loadList(COURSE_FILE, "courses");
    }
    
    private void loadDepartments() {
        departments = loadList(DEPARTMENT_FILE, "departments");
    }
    
    private void loadInstructors() {
        instructors = loadList(INSTRUCTOR_FILE, "instructors");
    }
    
    @SuppressWarnings("unchecked")
    private void loadEnrollments() {
        File file = new File(ENROLLMENT_FILE);
        enrollments = new ArrayList<>();
        
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object loadedData = ois.readObject();
                if (loadedData instanceof List) {
                    enrollments = (List<Enrollment>) loadedData;
                } else {
                    System.out.println("Warning: Enrollment data format mismatch. Starting with empty list.");
                }
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading enrollments: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Helper methods for finding objects by ID
    public Student findStudentById(int id) {
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }
    
    public Course findCourseById(int id) {
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            if (course.getId() == id) {
                return course;
            }
        }
        return null;
    }
    
    public Department findDepartmentById(int id) {
        for (int i = 0; i < departments.size(); i++) {
            Department department = departments.get(i);
            if (department.getId() == id) {
                return department;
            }
        }
        return null;
    }
    
    public Instructor findInstructorById(int id) {
        for (int i = 0; i < instructors.size(); i++) {
            Instructor instructor = instructors.get(i);
            if (instructor.getId() == id) {
                return instructor;
            }
        }
        return null;
    }
    
    public MyGenericList<Instructor> getInstructorsByDepartment(int departmentId) {
        MyGenericList<Instructor> departmentInstructors = new MyGenericList<>();
        for (int i = 0; i < instructors.size(); i++) {
            Instructor instructor = instructors.get(i);
            if (instructor.getDepartmentId() == departmentId) {
                departmentInstructors.add(instructor);
            }
        }
        return departmentInstructors;
    }
} 
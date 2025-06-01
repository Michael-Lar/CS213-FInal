package com.studentinfo;

import javax.swing.*;
import java.util.List;

import com.studentinfo.dao.DepartmentDAO;
import com.studentinfo.dao.ProfessorDAO;
import com.studentinfo.dao.StudentDAO;
import com.studentinfo.dao.CourseDAO;
import com.studentinfo.dao.EnrollmentDAO;

/**
 * Main application window containing all panels and data management.
 * Handles data persistence and provides access to shared data stores.
 */
class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    
    // Shared data stores using DAOs
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private DepartmentDAO departmentDAO;
    private ProfessorDAO professorDAO;
    private EnrollmentDAO enrollmentDAO;
    
    // Panels
    private StudentPanel studentPanel;
    private CoursePanel coursePanel;
    private DepartmentPanel departmentPanel;
    private ProfessorPanel professorPanel;
    private EnrollmentPanel enrollmentPanel;
    private GradePanel gradePanel;
    private ReportPanel reportPanel;

    public MainFrame() {
        setTitle("Student Information System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize data stores
        studentDAO = new StudentDAO();
        courseDAO = new CourseDAO();
        departmentDAO = new DepartmentDAO();
        professorDAO = new ProfessorDAO();
        enrollmentDAO = new EnrollmentDAO();
        
        // Initialize panels with shared data
        tabbedPane = new JTabbedPane();
        studentPanel = new StudentPanel(this);
        coursePanel = new CoursePanel(this);
        departmentPanel = new DepartmentPanel(this);
        professorPanel = new ProfessorPanel(this);
        enrollmentPanel = new EnrollmentPanel(this);
        gradePanel = new GradePanel(this);
        reportPanel = new ReportPanel(this);

        tabbedPane.addTab("Students", studentPanel);
        tabbedPane.addTab("Courses", coursePanel);
        tabbedPane.addTab("Departments", departmentPanel);
        tabbedPane.addTab("Professors", professorPanel);
        tabbedPane.addTab("Enrollments", enrollmentPanel);
        tabbedPane.addTab("Grades", gradePanel);
        tabbedPane.addTab("Reports", reportPanel);

        add(tabbedPane);
    }
    
    // Data access methods
    public List<Student> getStudents() {
        return this.studentDAO.getAllStudents();
    }
    
    public List<Course> getCourses() {
        return this.courseDAO.getAllCourses();
    }
    
    public List<Department> getDepartments() {
        return departmentDAO.getAllDepartments();
    }
    
    public List<Professor> getProfessors() {
        return professorDAO.getAllProfessors();
    }
    
    // Enrollment specific methods using EnrollmentDAO
    public List<Enrollment> getAllEnrollmentsFromDB() {
        return this.enrollmentDAO.getAllEnrollments();
    }

    public boolean addEnrollmentToDB(Enrollment enrollment) {
        return this.enrollmentDAO.addEnrollment(enrollment);
    }

    public Enrollment findEnrollmentInDB(int studentId, int courseId, String year, String semester) {
        return this.enrollmentDAO.findEnrollment(studentId, courseId, year, semester);
    }

    public List<Enrollment> getEnrollmentsForStudentFromDB(int studentId) {
        return this.enrollmentDAO.getEnrollmentsByStudentId(studentId);
    }

    public List<Enrollment> getEnrollmentsForCourseFromDB(int courseId) {
        return this.enrollmentDAO.getEnrollmentsByCourseId(courseId);
    }

    public boolean updateGradeInDB(int studentId, int courseId, String year, String semester, String grade) {
        return this.enrollmentDAO.updateGrade(studentId, courseId, year, semester, grade);
    }
    
    // Method to refresh all department combos
    public void refreshDepartmentCombos() {
        if (coursePanel != null) {
            coursePanel.updateDepartmentCombo();
            coursePanel.updateProfessorCombo();
        }
        if (professorPanel != null) {
            professorPanel.updateDepartmentCombo();
        }
    }
    
    // Add new method for professor updates
    public void refreshProfessorCombos() {
        if (coursePanel != null) {
            coursePanel.updateProfessorCombo();
        }
    }
    
    // Helper methods for finding objects by ID
    public Student findStudentById(int id) {
        return this.studentDAO.getStudentById(id);
    }
    
    public Course findCourseById(int id) {
        return this.courseDAO.getCourseById(id);
    }
    
    public Course findCourseByCourseNumber(String courseNumber) {
        return this.courseDAO.getCourseByCourseNumber(courseNumber);
    }
    
    public Department findDepartmentById(int id) {
        return departmentDAO.getDepartmentById(id);
    }
    
    public Professor findProfessorById(int id) {
        return professorDAO.getProfessorById(id);
    }
    
    public List<Professor> getProfessorsByDepartment(int departmentId) {
        return professorDAO.getProfessorsByDepartmentId(departmentId);
    }
} 
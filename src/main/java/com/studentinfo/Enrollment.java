package com.studentinfo;

/**
 * Represents a student's enrollment in a course for a specific semester.
 */
public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int courseId;
    private String year;
    private String semester;
    private String grade; // Null until grade is assigned

    // Constructor for new enrollments (grade starts as null)
    public Enrollment(int studentId, int courseId, String year, String semester) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.year = year;
        this.semester = semester;
        this.grade = null;
    }

    // Constructor for existing enrollments from database
    public Enrollment(int enrollmentId, int studentId, int courseId, String year, String semester, String grade) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.year = year;
        this.semester = semester;
        this.grade = grade;
    }

    // Getters and setters
    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getStudentId() { return studentId; }

    public int getCourseId() { return courseId; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
} 
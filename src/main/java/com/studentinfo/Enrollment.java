package com.studentinfo;

import java.io.Serializable;

/**
 * Enrollment record linking students to courses with grade tracking.
 * Implements Serializable for storage.
 */
class Enrollment implements Serializable {
    private static final long serialVersionUID = 1L;
    private int studentId;
    private int courseId;
    private String year;
    private String semester;
    private String grade; // Can be null if grade not assigned yet

    public Enrollment(int studentId, int courseId, String year, String semester) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.year = year;
        this.semester = semester;
        this.grade = null;
    }

    // Getters and setters
    public int getStudentId() { return studentId; }
    public int getCourseId() { return courseId; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
} 
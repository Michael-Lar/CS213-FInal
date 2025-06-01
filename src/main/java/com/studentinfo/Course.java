package com.studentinfo;

/**
 * Represents a course offering in the system.
 */
public class Course implements Comparable<Course> {
    private int id;
    private String courseNumber;
    private String courseName;
    private int credits;
    private int departmentId;
    private int professorId;

    public Course(int id, String courseNumber, String courseName, int credits, int departmentId, int professorId) {
        this.id = id;
        this.courseNumber = courseNumber;
        this.courseName = courseName;
        this.credits = credits;
        this.departmentId = departmentId;
        this.professorId = professorId;
    }

    public int getId() {
        return id;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCredits() {
        return credits;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public int getProfessorId() {
        return professorId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }

    @Override
    public String toString() {
        return courseNumber + " - " + courseName;
    }

    @Override
    public int compareTo(Course other) {
        // Sort by course number, fall back to ID if null
        if (this.courseNumber != null && other.courseNumber != null) {
            return this.courseNumber.compareTo(other.courseNumber);
        }
        return Integer.compare(this.id, other.id);
    }
} 
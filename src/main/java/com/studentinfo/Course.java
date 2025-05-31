package com.studentinfo;

import java.io.Serializable;

/**
 * Course information including credits and department/instructor assignments.
 * Implements Comparable for sorting and Serializable for storage.
 */
class Course implements Serializable, Comparable<Course> {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private int departmentId;
    private int instructorId;
    private int credits;

    public Course(int id, String name, int credits, int departmentId, int instructorId) {
        this.id = id;
        this.name = name;
        this.credits = credits;
        this.departmentId = departmentId;
        this.instructorId = instructorId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Course other) {
        return Integer.compare(this.id, other.id);
    }
} 
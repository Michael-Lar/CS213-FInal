package com.studentinfo;

import java.io.Serializable;

/**
 * Instructor information with department assignment and contact details.
 * Implements Comparable for sorting and Serializable for storage.
 */
class Instructor implements Serializable, Comparable<Instructor> {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String email;
    private String phone;
    private int departmentId;

    public Instructor(int id, String name, String email, String phone, int departmentId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.departmentId = departmentId;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    
    @Override
    public int compareTo(Instructor other) {
        return Integer.compare(this.id, other.id);
    }
    
    @Override
    public String toString() {
        return name;
    }
} 
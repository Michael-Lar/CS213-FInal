package com.studentinfo;

import java.io.Serializable;

/**
 * Department information with location and contact details.
 * Implements Comparable for sorting and Serializable for storage.
 */
class Department implements Serializable, Comparable<Department> {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String location;
    private String phone;

    public Department(int id, String name, String location, String phone) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.phone = phone;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    @Override
    public int compareTo(Department other) {
        return Integer.compare(this.id, other.id);
    }
    
    @Override
    public String toString() {
        return name;
    }
} 
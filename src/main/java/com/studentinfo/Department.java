package com.studentinfo;

/**
 * Represents a department within the institution.
 */
public class Department implements Comparable<Department> {
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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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
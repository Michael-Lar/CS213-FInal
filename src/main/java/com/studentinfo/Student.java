package com.studentinfo;

/**
 * Represents a student in the system.
 */
public class Student implements Comparable<Student> {
    private int id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;

    public Student(int id, String name, String address, String city, String state, String zip) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }
    
    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.id, other.id);
    }
    
    @Override
    public String toString() {
        return String.format("Student[ID: %d, Name: %s, Address: %s, %s, %s %s]", 
            id, name, address, city, state, zip);
    }
} 
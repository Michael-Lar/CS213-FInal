package com.studentinfo;

/**
 * Represents a professor/instructor in the system.
 */
public class Professor implements Comparable<Professor> {
    private int prof_id;
    private String prof_name;
    private String email;
    private String phone;
    private int dept_id; // Foreign key to Department

    public Professor(int prof_id, String prof_name, String email, String phone, int dept_id) {
        this.prof_id = prof_id;
        this.prof_name = prof_name;
        this.email = email;
        this.phone = phone;
        this.dept_id = dept_id;
    }

    public int getProf_id() {
        return prof_id;
    }

    public void setProf_id(int prof_id) {
        this.prof_id = prof_id;
    }

    public String getProf_name() {
        return prof_name;
    }

    public void setProf_name(String prof_name) {
        this.prof_name = prof_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getDept_id() {
        return dept_id;
    }

    public void setDept_id(int dept_id) {
        this.dept_id = dept_id;
    }
    
    @Override
    public int compareTo(Professor other) {
        return Integer.compare(this.prof_id, other.prof_id);
    }
    
    @Override
    public String toString() {
        return prof_name;
    }
} 
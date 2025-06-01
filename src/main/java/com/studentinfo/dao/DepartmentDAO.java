package com.studentinfo.dao;

import com.studentinfo.Department;
import com.studentinfo.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public boolean addDepartment(Department department) {
        String sql = "INSERT INTO Department (dept_name, location, phone) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, department.getName());
            pstmt.setString(2, department.getLocation());
            pstmt.setString(3, department.getPhone());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        department.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("dept_name")) {
                throw new IllegalArgumentException("A department with this name already exists.");
            }
            throw new IllegalArgumentException("Error adding department: " + e.getMessage());
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error adding department: " + e.getMessage());
        }
        return false;
    }

    public Department getDepartmentById(int deptId) {
        String sql = "SELECT dept_id, dept_name, location, phone FROM Department WHERE dept_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Department(
                        rs.getInt("dept_id"),
                        rs.getString("dept_name"),
                        rs.getString("location"),
                        rs.getString("phone")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT dept_id, dept_name, location, phone FROM Department ORDER BY dept_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                departments.add(new Department(
                        rs.getInt("dept_id"),
                        rs.getString("dept_name"),
                        rs.getString("location"),
                        rs.getString("phone")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    public boolean updateDepartment(Department department) {
        String sql = "UPDATE Department SET dept_name = ?, location = ?, phone = ? WHERE dept_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, department.getName());
            pstmt.setString(2, department.getLocation());
            pstmt.setString(3, department.getPhone());
            pstmt.setInt(4, department.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDepartment(int deptId) {
        String sql = "DELETE FROM Department WHERE dept_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, deptId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
} 
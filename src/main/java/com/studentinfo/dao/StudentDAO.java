package com.studentinfo.dao;

import com.studentinfo.Student;
import com.studentinfo.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean addStudent(Student student) {
        String sql = "{CALL add_student_proc(?, ?, ?, ?, ?)}";
        try (Connection conn = DatabaseConnector.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, student.getName());
            stmt.setString(2, student.getAddress());
            stmt.setString(3, student.getCity());
            stmt.setString(4, student.getState());
            stmt.setString(5, student.getZip());

            stmt.executeUpdate();
            
            // Retrieve the generated student ID using LAST_INSERT_ID()
            try (Statement idStmt = conn.createStatement()) {
                ResultSet rs = idStmt.executeQuery("SELECT LAST_INSERT_ID()");
                if (rs.next()) {
                    student.setId(rs.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
    }

    public Student getStudentById(int studentId) {
        String sql = "SELECT * FROM Student WHERE student_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("zip")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM Student ORDER BY name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("zip")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return students;
    }

    public boolean updateStudent(Student student) {
        String sql = "UPDATE Student SET name = ?, address = ?, city = ?, state = ?, zip = ? WHERE student_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getName());
            stmt.setString(2, student.getAddress());
            stmt.setString(3, student.getCity());
            stmt.setString(4, student.getState());
            stmt.setString(5, student.getZip());
            stmt.setInt(6, student.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
    }

    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM Student WHERE student_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
    }
} 
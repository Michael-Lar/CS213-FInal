package com.studentinfo.dao;

import com.studentinfo.Enrollment;
import com.studentinfo.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public boolean addEnrollment(Enrollment enrollment) {
        String sql = "INSERT INTO Enrollment (student_id, course_id, year, semester, grade) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setString(3, enrollment.getYear());
            pstmt.setString(4, enrollment.getSemester());
            pstmt.setString(5, enrollment.getGrade());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        enrollment.setEnrollmentId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return false;
    }

    public Enrollment findEnrollment(int studentId, int courseId, String year, String semester) {
        String sql = "SELECT * FROM Enrollment WHERE student_id = ? AND course_id = ? AND year = ? AND semester = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, year);
            pstmt.setString(4, semester);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getInt("student_id"),
                            rs.getInt("course_id"),
                            rs.getString("year"),
                            rs.getString("semester"),
                            rs.getString("grade")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Enrollment> getEnrollmentsByStudentId(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM Enrollment WHERE student_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getInt("student_id"),
                            rs.getInt("course_id"),
                            rs.getString("year"),
                            rs.getString("semester"),
                            rs.getString("grade")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return enrollments;
    }

    public List<Enrollment> getEnrollmentsByCourseId(int courseId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM Enrollment WHERE course_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getInt("student_id"),
                            rs.getInt("course_id"),
                            rs.getString("year"),
                            rs.getString("semester"),
                            rs.getString("grade")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return enrollments;
    }

    public boolean updateGrade(int studentId, int courseId, String year, String semester, String grade) {
        String sql = "UPDATE Enrollment SET grade = ? WHERE student_id = ? AND course_id = ? AND year = ? AND semester = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, grade);
            pstmt.setInt(2, studentId);
            pstmt.setInt(3, courseId);
            pstmt.setString(4, year);
            pstmt.setString(5, semester);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
    }

    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM Enrollment";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                enrollments.add(new Enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getInt("course_id"),
                        rs.getString("year"),
                        rs.getString("semester"),
                        rs.getString("grade")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return enrollments;
    }
} 
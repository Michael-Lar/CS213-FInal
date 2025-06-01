package com.studentinfo.dao;

import com.studentinfo.Course;
import com.studentinfo.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public boolean addCourse(Course course) {
        String sql = "INSERT INTO Course (course_number, course_name, credits, dept_id, prof_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, course.getCourseNumber());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getDepartmentId());
            stmt.setInt(5, course.getProfessorId()); // Changed from getInstructorId()

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        course.setId(generatedKeys.getInt(1)); // Set the auto-generated course_id
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
    }

    public Course getCourseById(int courseId) {
        String sql = "SELECT * FROM Course WHERE course_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_number"),
                        rs.getString("course_name"),
                        rs.getInt("credits"),
                        rs.getInt("dept_id"),
                        rs.getInt("prof_id")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return null;
    }

    public Course getCourseByCourseNumber(String courseNumber) {
        String sql = "SELECT * FROM Course WHERE course_number = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, courseNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_number"),
                        rs.getString("course_name"),
                        rs.getInt("credits"),
                        rs.getInt("dept_id"),
                        rs.getInt("prof_id")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Course ORDER BY course_number"; // Or course_name
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_number"),
                        rs.getString("course_name"),
                        rs.getInt("credits"),
                        rs.getInt("dept_id"),
                        rs.getInt("prof_id")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return courses;
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE Course SET course_number = ?, course_name = ?, credits = ?, dept_id = ?, prof_id = ? WHERE course_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, course.getCourseNumber());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getDepartmentId());
            stmt.setInt(5, course.getProfessorId()); // Changed from getInstructorId()
            stmt.setInt(6, course.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
    }

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM Course WHERE course_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
    }
} 
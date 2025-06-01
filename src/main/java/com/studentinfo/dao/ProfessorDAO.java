package com.studentinfo.dao;

import com.studentinfo.Professor;
import com.studentinfo.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfessorDAO {

    public boolean addProfessor(Professor professor) {
        String sql = "INSERT INTO Professor (prof_name, email, phone, dept_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, professor.getProf_name());
            pstmt.setString(2, professor.getEmail());
            pstmt.setString(3, professor.getPhone());
            pstmt.setInt(4, professor.getDept_id());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        professor.setProf_id(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return false;
    }

    public Professor getProfessorById(int profId) {
        String sql = "SELECT * FROM Professor WHERE prof_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Professor(
                    rs.getInt("prof_id"),
                    rs.getString("prof_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getInt("dept_id")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Professor> getAllProfessors() {
        List<Professor> professors = new ArrayList<>();
        String sql = "SELECT * FROM Professor ORDER BY prof_name";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                professors.add(new Professor(
                    rs.getInt("prof_id"),
                    rs.getString("prof_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getInt("dept_id")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return professors;
    }

    public List<Professor> getProfessorsByDepartmentId(int deptId) {
        List<Professor> professors = new ArrayList<>();
        String sql = "SELECT * FROM Professor WHERE dept_id = ? ORDER BY prof_name";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                professors.add(new Professor(
                    rs.getInt("prof_id"),
                    rs.getString("prof_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getInt("dept_id")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
        return professors;
    }

    public boolean updateProfessor(Professor professor) {
        String sql = "UPDATE Professor SET prof_name = ?, email = ?, phone = ?, dept_id = ? WHERE prof_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, professor.getProf_name());
            pstmt.setString(2, professor.getEmail());
            pstmt.setString(3, professor.getPhone());
            pstmt.setInt(4, professor.getDept_id());
            pstmt.setInt(5, professor.getProf_id());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
    }

    public boolean deleteProfessor(int profId) {
        String sql = "DELETE FROM Professor WHERE prof_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
        }
    }
} 
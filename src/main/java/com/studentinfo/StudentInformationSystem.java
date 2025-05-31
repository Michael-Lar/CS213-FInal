package com.studentinfo;

import javax.swing.*;

/**
 * Student Information System - Manages student records, courses, enrollments, and grades.
 * Uses Swing for UI and file-based storage for persistence.
 */
public class StudentInformationSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
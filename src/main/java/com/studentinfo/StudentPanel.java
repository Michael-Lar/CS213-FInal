package com.studentinfo;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import com.studentinfo.dao.StudentDAO;

/**
 * Student management panel with ZIP code lookup integration.
 * Handles adding, editing, and searching student records.
 */
class StudentPanel extends JPanel {
    private MainFrame mainFrame;
    private StudentDAO studentDAO;
    private JTextField idField, nameField, addressField, cityField, stateField, zipField;
    private JButton addButton, searchButton, editButton, resetButton, lookupZipButton;
    private JLabel statusLabel;

    public StudentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.studentDAO = new StudentDAO();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Student ID:"));
        idField = new JTextField();
        idField.setEditable(false);
        idField.setBackground(UIManager.getColor("TextField.inactiveBackground"));
        formPanel.add(idField);

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        formPanel.add(new JLabel("City:"));
        cityField = new JTextField();
        formPanel.add(cityField);

        formPanel.add(new JLabel("State:"));
        stateField = new JTextField();
        formPanel.add(stateField);

        formPanel.add(new JLabel("ZIP:"));
        JPanel zipPanel = new JPanel(new BorderLayout());
        zipField = new JTextField();
        lookupZipButton = new JButton("Lookup");
        lookupZipButton.addActionListener(e -> {
            String zipCode = zipField.getText().trim();
            if (!zipCode.isEmpty()) {
                lookupZipCode(zipCode);
            }
        });
        zipPanel.add(zipField, BorderLayout.CENTER);
        zipPanel.add(lookupZipButton, BorderLayout.EAST);
        formPanel.add(zipPanel);

        statusLabel = new JLabel(" ");
        formPanel.add(statusLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add Student");
        searchButton = new JButton("Search");
        editButton = new JButton("Edit Student");
        resetButton = new JButton("Reset");

        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(editButton);
        buttonPanel.add(resetButton);

        // Add components to main panel
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> addStudent());
        searchButton.addActionListener(e -> searchStudent());
        editButton.addActionListener(e -> editStudent());
        resetButton.addActionListener(e -> resetFields());
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String city = cityField.getText().trim();
        String state = stateField.getText().trim();
        String zip = zipField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() || zip.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields except ID are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student newStudent = new Student(0, name, address, city, state, zip);

        if (studentDAO.addStudent(newStudent)) {
            JOptionPane.showMessageDialog(this, "Student added successfully. New ID: " + newStudent.getId(), "Success", JOptionPane.INFORMATION_MESSAGE);
            idField.setText(String.valueOf(newStudent.getId()));
            idField.setEditable(false);
            idField.setBackground(UIManager.getColor("TextField.inactiveBackground"));
            resetFields(false);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add student to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchStudent() {
        try {
            if (idField.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(this, "Please enter a Student ID to search.", "Info", JOptionPane.INFORMATION_MESSAGE);
                idField.setEditable(true);
                idField.setBackground(UIManager.getColor("TextField.background"));
                idField.requestFocus();
                return;
            }
            int id = Integer.parseInt(idField.getText().trim());
            Student student = studentDAO.getStudentById(id);

            if (student != null) {
                idField.setEditable(false);
                idField.setBackground(UIManager.getColor("TextField.inactiveBackground"));
                nameField.setText(student.getName());
                addressField.setText(student.getAddress());
                cityField.setText(student.getCity());
                stateField.setText(student.getState());
                zipField.setText(student.getZip());
                statusLabel.setText("Student found. ID: " + student.getId());
                editButton.setEnabled(true);
                addButton.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, "Student ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                resetFields(true);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            resetFields(true);
        }
    }

    private void editStudent() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String city = cityField.getText().trim();
            String state = stateField.getText().trim();
            String zip = zipField.getText().trim();

            if (name.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() || zip.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student studentToUpdate = new Student(id, name, address, city, state, zip);

            if (studentDAO.updateStudent(studentToUpdate)) {
                JOptionPane.showMessageDialog(this, "Student updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetFields(true);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update student. Make sure the ID is correct.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format in ID field. This should not happen if search was performed first.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        resetFields(true);
    }

    private void resetFields(boolean makeIdEditable) {
        if (makeIdEditable) {
            idField.setEditable(true);
            idField.setBackground(UIManager.getColor("TextField.background"));
            idField.setText("");
        } else {
            // If not making ID editable, it means an add operation just completed
            // and we want to keep the ID displayed and non-editable.
            // We might still clear other fields or not, depending on desired UX.
            // For now, let's clear other fields for consistency.
        }
        nameField.setText("");
        addressField.setText("");
        cityField.setText("");
        stateField.setText("");
        zipField.setText("");
        statusLabel.setText(" ");
        editButton.setEnabled(false);
        addButton.setEnabled(true);
        if (makeIdEditable) idField.requestFocus();
    }

    /**
     * Looks up city and state from ZIP code using external API.
     * Includes retry mechanism and error handling.
     * @param zipCode ZIP code to look up
     */
    private void lookupZipCode(String zipCode) {
        int maxRetries = 3;
        int retryCount = 0;
        boolean success = false;

        while (!success && retryCount < maxRetries) {
            try {
                URL url = new URL("https://api.zippopotam.us/us/" + zipCode);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000); // 5 second timeout
                
                int responseCode = conn.getResponseCode();
                
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    // Parse JSON response
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray places = jsonObject.getJSONArray("places");
                    
                    if (places.length() > 0) {
                        JSONObject place = places.getJSONObject(0);
                        String city = place.getString("place name");
                        String state = place.getString("state");
                        
                        // Update the form fields
                        cityField.setText(city);
                        stateField.setText(state);
                        success = true;
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "No location found for zip code: " + zipCode, 
                            "Location Not Found", 
                            JOptionPane.WARNING_MESSAGE);
                        break;
                    }
                } else if (responseCode == 404) {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid zip code: " + zipCode, 
                        "Invalid Zip Code", 
                        JOptionPane.WARNING_MESSAGE);
                    break;
                } else {
                    throw new IOException("HTTP Error: " + responseCode);
                }
            } catch (java.net.SocketTimeoutException e) {
                retryCount++;
                if (retryCount < maxRetries) {
                    JOptionPane.showMessageDialog(this, 
                        "Connection timeout. Retrying... (Attempt " + retryCount + " of " + maxRetries + ")", 
                        "Connection Timeout", 
                        JOptionPane.WARNING_MESSAGE);
                    try {
                        Thread.sleep(1000); // Wait 1 second before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to connect after " + maxRetries + " attempts. Please check your internet connection.", 
                        "Connection Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (java.net.UnknownHostException e) {
                JOptionPane.showMessageDialog(this, 
                    "Cannot connect to the server. Please check your internet connection.", 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
                break;
            } catch (org.json.JSONException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error parsing response from server. Please try again.", 
                    "Invalid Response", 
                    JOptionPane.ERROR_MESSAGE);
                break;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error looking up zip code: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }
} 
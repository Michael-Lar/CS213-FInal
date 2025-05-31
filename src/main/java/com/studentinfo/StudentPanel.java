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


/**
 * Student management panel with ZIP code lookup integration.
 * Handles adding, editing, and searching student records.
 */
class StudentPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField idField, nameField, addressField, cityField, stateField, zipField;
    private JButton addButton, searchButton, editButton, resetButton, lookupZipButton;
    private JLabel statusLabel;

    public StudentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Student ID:"));
        idField = new JTextField();
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
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String city = cityField.getText().trim();
            String state = stateField.getText().trim();
            String zip = zipField.getText().trim();

            // Validate fields
            if (name.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() || zip.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if student ID already exists
            if (mainFrame.getStudents().size() > 0 && mainFrame.findStudentById(id) != null) {
                JOptionPane.showMessageDialog(this, "Student ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create new student and add to list
            Student student = new Student(id, name, address, city, state, zip);
            mainFrame.getStudents().add(student);
            mainFrame.saveStudents();

            JOptionPane.showMessageDialog(this, "Student added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            resetFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchStudent() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Student student = mainFrame.findStudentById(id);

            if (student != null) {
                // Make ID field non-editable after successful search
                idField.setEditable(false);
                idField.setBackground(UIManager.getColor("TextField.inactiveBackground"));
                
                // Populate other fields
                nameField.setText(student.getName());
                addressField.setText(student.getAddress());
                cityField.setText(student.getCity());
                stateField.setText(student.getState());
                zipField.setText(student.getZip());
                statusLabel.setText("Student found.");
                
                // Enable edit button and disable add button
                if (editButton != null) editButton.setEnabled(true);
                if (addButton != null) addButton.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, "Student ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                resetFields(); // Reset all fields if student not found
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates student record after validation.
     * Disables ID field during edit mode.
     */
    private void editStudent() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Student student = mainFrame.findStudentById(id);

            if (student != null) {
                // Lock ID field
                idField.setEditable(false);
                
                // Update student information
                String name = nameField.getText().trim();
                String address = addressField.getText().trim();
                String city = cityField.getText().trim();
                String state = stateField.getText().trim();
                String zip = zipField.getText().trim();

                // Validate fields
                if (name.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty() || zip.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                student.setName(name);
                student.setAddress(address);
                student.setCity(city);
                student.setState(state);
                student.setZip(zip);
                mainFrame.saveStudents();

                JOptionPane.showMessageDialog(this, "Student updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetFields();
            } else {
                JOptionPane.showMessageDialog(this, "Student ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        // Make ID field editable again
        idField.setEditable(true);
        idField.setBackground(UIManager.getColor("TextField.background"));
        
        // Clear all fields
        idField.setText("");
        nameField.setText("");
        addressField.setText("");
        cityField.setText("");
        stateField.setText("");
        zipField.setText("");
        statusLabel.setText(" ");
        
        // Reset button states
        if (editButton != null) editButton.setEnabled(true);
        if (addButton != null) addButton.setEnabled(true);
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
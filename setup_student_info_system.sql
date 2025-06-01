-- Create the application database if it doesn't already exist
CREATE DATABASE IF NOT EXISTS student_info_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create the application user and grant privileges
-- IMPORTANT: Replace 'YOUR_NEW_STRONG_RDS_PASSWORD_HERE' with your actual strong password.
CREATE USER IF NOT EXISTS 'student_app_user'@'%' IDENTIFIED BY 'YOUR_NEW_STRONG_RDS_PASSWORD_HERE';
GRANT ALL PRIVILEGES ON student_info_system.* TO 'student_app_user'@'%';
FLUSH PRIVILEGES;

-- Use the application database
USE student_info_system;

-- Create All Application Tables

-- Department Table
CREATE TABLE IF NOT EXISTS Department (
    dept_id INT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(255) NOT NULL UNIQUE,
    location VARCHAR(255),
    phone VARCHAR(20)
);

-- Professor Table
CREATE TABLE IF NOT EXISTS Professor (
    prof_id INT AUTO_INCREMENT PRIMARY KEY,
    prof_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    dept_id INT,
    FOREIGN KEY (dept_id) REFERENCES Department(dept_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- Student Table
CREATE TABLE IF NOT EXISTS Student (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    zip VARCHAR(20)
);

-- Course Table
CREATE TABLE IF NOT EXISTS Course (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_number VARCHAR(50) NOT NULL UNIQUE,
    course_name VARCHAR(255) NOT NULL,
    credits INT NOT NULL,
    dept_id INT,
    prof_id INT,
    FOREIGN KEY (dept_id) REFERENCES Department(dept_id)
        ON DELETE SET NULL ON UPDATE CASCADE, -- Or RESTRICT if a department cannot be deleted if courses exist
    FOREIGN KEY (prof_id) REFERENCES Professor(prof_id)
        ON DELETE SET NULL ON UPDATE CASCADE -- Allows a course to exist without an assigned professor temporarily
);

-- Enrollment Table
CREATE TABLE IF NOT EXISTS Enrollment (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    year VARCHAR(4) NOT NULL,
    semester VARCHAR(50) NOT NULL,
    grade VARCHAR(2), -- e.g., A, B+, C-, etc. Can be NULL if not graded yet
    FOREIGN KEY (student_id) REFERENCES Student(student_id)
        ON DELETE CASCADE ON UPDATE CASCADE, -- If a student is deleted, their enrollments are removed
    FOREIGN KEY (course_id) REFERENCES Course(course_id)
        ON DELETE CASCADE ON UPDATE CASCADE, -- If a course is deleted, enrollments in it are removed
    UNIQUE (student_id, course_id, year, semester) -- Ensures a student cannot enroll in the same course in the same semester/year multiple times
);

-- Create the Stored Procedure for adding a student
DELIMITER $$

CREATE PROCEDURE IF NOT EXISTS add_student_proc(
    IN p_name VARCHAR(255),
    IN p_address VARCHAR(255),
    IN p_city VARCHAR(100),
    IN p_state VARCHAR(100),
    IN p_zip VARCHAR(20)
)
BEGIN
    INSERT INTO Student (name, address, city, state, zip)
    VALUES (p_name, p_address, p_city, p_state, p_zip);
END$$

DELIMITER ;

-- End of script 
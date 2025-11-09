-- =====================================================
-- Online Course Enrollment System - Database Setup
-- =====================================================
-- Database: online_course_db
-- User: root
-- Password: Saikrishna2005
-- =====================================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS online_course_db;
USE online_course_db;

-- =====================================================
-- Create Tables (if Hibernate hasn't created them yet)
-- =====================================================

-- Create students table
CREATE TABLE IF NOT EXISTS students (
    student_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create courses table
CREATE TABLE IF NOT EXISTS courses (
    course_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    description TEXT,
    instructor VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    fee DECIMAL(10, 2) NOT NULL,
    capacity INT NOT NULL DEFAULT 50,
    enrolled_count INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create enrollments table
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    enrollment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment (student_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create payments table
CREATE TABLE IF NOT EXISTS payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Dummy Data Insertion
-- =====================================================

-- Insert sample courses (using INSERT IGNORE to avoid duplicates)
INSERT IGNORE INTO courses (course_name, description, instructor, start_date, end_date, fee, capacity, enrolled_count) VALUES
('Java Programming Fundamentals', 
 'Learn core Java concepts including OOP principles, data structures, and exception handling. Perfect for beginners.', 
 'Dr. John Smith', 
 '2024-02-01', 
 '2024-05-31', 
 299.99, 
 50, 
 0),

('Advanced Web Development with JSF and Hibernate', 
 'Master enterprise Java web development using JSF, Hibernate ORM, and MySQL. Build scalable web applications.', 
 'Prof. Jane Doe', 
 '2024-03-01', 
 '2024-06-30', 
 399.99, 
 40, 
 0),

('Database Design and Management', 
 'Comprehensive course on MySQL database design, optimization, and administration. Learn SQL queries and performance tuning.', 
 'Dr. Robert Johnson', 
 '2024-02-15', 
 '2024-06-15', 
 349.99, 
 45, 
 0),

('Spring Framework Essentials', 
 'Learn Spring Core, Spring MVC, and Spring Boot for building enterprise Java applications.', 
 'Prof. Sarah Williams', 
 '2024-03-15', 
 '2024-07-15', 
 449.99, 
 35, 
 0),

('Full Stack Java Development', 
 'Complete full-stack development course covering frontend, backend, and database integration.', 
 'Dr. Michael Brown', 
 '2024-04-01', 
 '2024-08-01', 
 599.99, 
 30, 
 0),

('Microservices Architecture', 
 'Learn to design and implement microservices using Java, REST APIs, and cloud technologies.', 
 'Prof. Emily Davis', 
 '2024-04-15', 
 '2024-08-30', 
 549.99, 
 25, 
 0),

('Java Enterprise Edition (Java EE)', 
 'Master Java EE technologies including JPA, CDI, and web services for enterprise applications.', 
 'Dr. James Wilson', 
 '2024-05-01', 
 '2024-09-30', 
 499.99, 
 40, 
 0),

('Data Structures and Algorithms in Java', 
 'Advanced course on data structures, algorithms, and problem-solving techniques using Java.', 
 'Prof. Lisa Anderson', 
 '2024-05-15', 
 '2024-10-15', 
 379.99, 
 50, 
 0);

-- Insert sample students (using INSERT IGNORE to avoid duplicates)
-- Note: Passwords are plain text for demo - in production, use hashed passwords
INSERT IGNORE INTO students (first_name, last_name, email, password, phone, address) VALUES
('Alice', 'Johnson', 'alice.johnson@email.com', 'Password123!', '555-0101', '123 Main St, City, State 12345'),
('Bob', 'Smith', 'bob.smith@email.com', 'Password123!', '555-0102', '456 Oak Ave, City, State 12346'),
('Charlie', 'Brown', 'charlie.brown@email.com', 'Password123!', '555-0103', '789 Pine Rd, City, State 12347'),
('Diana', 'Davis', 'diana.davis@email.com', 'Password123!', '555-0104', '321 Elm St, City, State 12348'),
('Edward', 'Wilson', 'edward.wilson@email.com', 'Password123!', '555-0105', '654 Maple Dr, City, State 12349');

-- Insert Admin User
-- Admin credentials: admin@course.com / Admin123!
INSERT IGNORE INTO students (first_name, last_name, email, password, phone, address) VALUES
('Admin', 'User', 'admin@course.com', 'Admin123!', '555-0000', 'Admin Office, System Admin');

-- Note: Enrollments and Payments will be created through the application
-- You can manually insert test data if needed:

-- Sample enrollment (uncomment after tables are created by Hibernate)
-- INSERT INTO enrollments (student_id, course_id, enrollment_date, status) VALUES
-- (1, 1, NOW(), 'ACTIVE'),
-- (1, 2, NOW(), 'ACTIVE'),
-- (2, 1, NOW(), 'ACTIVE'),
-- (3, 3, NOW(), 'ACTIVE');

-- Sample payment (uncomment after tables are created by Hibernate)
-- INSERT INTO payments (student_id, course_id, amount, payment_date, payment_method, status, transaction_id) VALUES
-- (1, 1, 299.99, NOW(), 'CREDIT_CARD', 'COMPLETED', 'TXN-001'),
-- (1, 2, 399.99, NOW(), 'PAYPAL', 'COMPLETED', 'TXN-002'),
-- (2, 1, 299.99, NOW(), 'DEBIT_CARD', 'COMPLETED', 'TXN-003');

-- =====================================================
-- Verification Queries
-- =====================================================

-- Verify courses
SELECT 'Courses' AS TableName, COUNT(*) AS RecordCount FROM courses;

-- Verify students
SELECT 'Students' AS TableName, COUNT(*) AS RecordCount FROM students;

-- Verify enrollments (if any)
SELECT 'Enrollments' AS TableName, COUNT(*) AS RecordCount FROM enrollments;

-- Verify payments (if any)
SELECT 'Payments' AS TableName, COUNT(*) AS RecordCount FROM payments;

-- Display all courses
SELECT course_id, course_name, instructor, fee, capacity, enrolled_count FROM courses;

-- Display all students
SELECT student_id, first_name, last_name, email FROM students;


package com.example.onlinecourse.service;

import com.example.onlinecourse.dao.StudentDAO;
import com.example.onlinecourse.entity.Student;

import java.util.List;

/**
 * Service layer for Student business logic.
 * Provides business operations on top of StudentDAO.
 */
public class StudentService {
    private StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    /**
     * Register a new student.
     *
     * @param student Student to register
     * @return Registered student
     * @throws RuntimeException if email already exists
     */
    public Student registerStudent(Student student) {
        if (studentDAO.emailExists(student.getEmail())) {
            throw new RuntimeException("Email already exists: " + student.getEmail());
        }
        return studentDAO.save(student);
    }

    /**
     * Authenticate student by email and password.
     *
     * @param email Student email
     * @param password Student password
     * @return Student if authenticated, null otherwise
     */
    public Student authenticate(String email, String password) {
        Student student = studentDAO.findByEmail(email);
        if (student != null && student.getPassword().equals(password)) {
            return student;
        }
        return null;
    }

    /**
     * Get student by ID.
     *
     * @param id Student ID
     * @return Student entity
     */
    public Student getStudentById(Long id) {
        return studentDAO.findById(id);
    }

    /**
     * Get student by email.
     *
     * @param email Student email
     * @return Student entity
     */
    public Student getStudentByEmail(String email) {
        return studentDAO.findByEmail(email);
    }

    /**
     * Get all students.
     *
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    /**
     * Update student.
     *
     * @param student Student to update
     * @return Updated student
     */
    public Student updateStudent(Student student) {
        return studentDAO.update(student);
    }

    /**
     * Delete student.
     *
     * @param id Student ID
     */
    public void deleteStudent(Long id) {
        studentDAO.delete(id);
    }

    /**
     * Check if email exists.
     *
     * @param email Email to check
     * @return true if email exists
     */
    public boolean emailExists(String email) {
        return studentDAO.emailExists(email);
    }
}


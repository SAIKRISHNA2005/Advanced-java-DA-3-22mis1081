package com.example.onlinecourse.service;

import com.example.onlinecourse.dao.CourseDAO;
import com.example.onlinecourse.dao.EnrollmentDAO;
import com.example.onlinecourse.dao.StudentDAO;
import com.example.onlinecourse.entity.Course;
import com.example.onlinecourse.entity.Enrollment;
import com.example.onlinecourse.entity.Student;
import com.example.onlinecourse.entity.Enrollment.EnrollmentStatus;

import java.util.Date;
import java.util.List;

/**
 * Service layer for Enrollment business logic.
 * Provides business operations on top of EnrollmentDAO.
 */
public class EnrollmentService {
    private EnrollmentDAO enrollmentDAO;
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;

    public EnrollmentService() {
        this.enrollmentDAO = new EnrollmentDAO();
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
    }

    /**
     * Enroll a student in a course.
     *
     * @param studentId Student ID
     * @param courseId Course ID
     * @return Created enrollment
     * @throws RuntimeException if student or course not found, or already enrolled, or course is full
     */
    public Enrollment enrollStudent(Long studentId, Long courseId) {
        Student student = studentDAO.findById(studentId);
        if (student == null) {
            throw new RuntimeException("Student not found with ID: " + studentId);
        }

        Course course = courseDAO.findById(courseId);
        if (course == null) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }

        // Check if already enrolled
        Enrollment existing = enrollmentDAO.findByStudentAndCourse(studentId, courseId);
        if (existing != null && existing.getStatus() == EnrollmentStatus.ACTIVE) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        // Check if course is available
        if (!course.isAvailable()) {
            throw new RuntimeException("Course is full. Cannot enroll more students.");
        }

        Enrollment enrollment = new Enrollment(student, course);
        enrollment.setEnrollmentDate(new Date());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        return enrollmentDAO.save(enrollment);
    }

    /**
     * Cancel an enrollment.
     *
     * @param enrollmentId Enrollment ID
     * @return Updated enrollment
     * @throws RuntimeException if enrollment not found
     */
    public Enrollment cancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentDAO.findById(enrollmentId);
        if (enrollment == null) {
            throw new RuntimeException("Enrollment not found with ID: " + enrollmentId);
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        return enrollmentDAO.update(enrollment);
    }

    /**
     * Get enrollment by ID.
     *
     * @param id Enrollment ID
     * @return Enrollment entity
     */
    public Enrollment getEnrollmentById(Long id) {
        return enrollmentDAO.findById(id);
    }

    /**
     * Get all enrollments.
     *
     * @return List of all enrollments
     */
    public List<Enrollment> getAllEnrollments() {
        return enrollmentDAO.findAll();
    }

    /**
     * Get enrollments by student ID.
     *
     * @param studentId Student ID
     * @return List of enrollments for the student
     */
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentDAO.findByStudentId(studentId);
    }

    /**
     * Get enrollments by course ID.
     *
     * @param courseId Course ID
     * @return List of enrollments for the course
     */
    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentDAO.findByCourseId(courseId);
    }

    /**
     * Check if student is enrolled in a course.
     *
     * @param studentId Student ID
     * @param courseId Course ID
     * @return true if enrolled
     */
    public boolean isEnrolled(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentDAO.findByStudentAndCourse(studentId, courseId);
        return enrollment != null && enrollment.getStatus() == EnrollmentStatus.ACTIVE;
    }
}


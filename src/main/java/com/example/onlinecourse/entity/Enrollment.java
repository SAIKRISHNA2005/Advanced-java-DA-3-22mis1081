package com.example.onlinecourse.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Enrollment entity representing the many-to-many relationship between Student and Course.
 * Maps to the 'enrollments' table in the database.
 */
@Entity
@Table(name = "enrollments")
public class Enrollment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "Course is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "enrollment_date", nullable = false)
    private Date enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status;

    // Default constructor
    public Enrollment() {
        this.enrollmentDate = new Date();
        this.status = EnrollmentStatus.ACTIVE;
    }

    // Constructor with parameters
    public Enrollment(Student student, Course course) {
        this();
        this.student = student;
        this.course = course;
    }

    // Getters and Setters
    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", student=" + (student != null ? student.getEmail() : "null") +
                ", course=" + (course != null ? course.getCourseName() : "null") +
                ", enrollmentDate=" + enrollmentDate +
                ", status=" + status +
                '}';
    }

    /**
     * Enumeration for enrollment status
     */
    public enum EnrollmentStatus {
        ACTIVE, CANCELLED, COMPLETED
    }
}


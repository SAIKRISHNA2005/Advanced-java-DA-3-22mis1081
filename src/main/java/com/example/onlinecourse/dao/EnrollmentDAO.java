package com.example.onlinecourse.dao;

import com.example.onlinecourse.entity.Enrollment;
import com.example.onlinecourse.entity.Student;
import com.example.onlinecourse.entity.Course;
import com.example.onlinecourse.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object for Enrollment entity.
 * Provides CRUD operations for Enrollment management.
 */
public class EnrollmentDAO {
    private SessionFactory sessionFactory;

    public EnrollmentDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    /**
     * Save a new enrollment.
     *
     * @param enrollment Enrollment entity to save
     * @return Saved enrollment with generated ID
     */
    public Enrollment save(Enrollment enrollment) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(enrollment);
            // Flush to get the generated ID
            session.flush();
            // Update course enrolled count by querying actual count
            Course course = enrollment.getCourse();
            if (course != null) {
                Query<Long> countQuery = session.createQuery(
                        "SELECT COUNT(*) FROM Enrollment WHERE course.courseId = :courseId AND status = 'ACTIVE'",
                        Long.class);
                countQuery.setParameter("courseId", course.getCourseId());
                Long count = countQuery.uniqueResult();
                course.setEnrolledCount(count != null ? count.intValue() : 0);
                session.update(course);
            }
            // Reload with associations to avoid lazy loading issues
            Query<Enrollment> query = session.createQuery(
                    "SELECT e FROM Enrollment e LEFT JOIN FETCH e.course LEFT JOIN FETCH e.student WHERE e.enrollmentId = :id",
                    Enrollment.class);
            query.setParameter("id", enrollment.getEnrollmentId());
            Enrollment savedEnrollment = query.uniqueResult();
            transaction.commit();
            return savedEnrollment != null ? savedEnrollment : enrollment;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving enrollment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    /**
     * Find enrollment by ID.
     *
     * @param id Enrollment ID
     * @return Enrollment entity or null if not found
     */
    public Enrollment findById(Long id) {
        Session session = sessionFactory.openSession();
        try {
            Query<Enrollment> query = session.createQuery(
                    "SELECT e FROM Enrollment e LEFT JOIN FETCH e.course LEFT JOIN FETCH e.student WHERE e.enrollmentId = :id",
                    Enrollment.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Get all enrollments.
     *
     * @return List of all enrollments
     */
    public List<Enrollment> findAll() {
        Session session = sessionFactory.openSession();
        try {
            Query<Enrollment> query = session.createQuery(
                    "SELECT DISTINCT e FROM Enrollment e LEFT JOIN FETCH e.course LEFT JOIN FETCH e.student ORDER BY e.enrollmentDate DESC",
                    Enrollment.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Get enrollments by student ID.
     *
     * @param studentId Student ID
     * @return List of enrollments for the student
     */
    public List<Enrollment> findByStudentId(Long studentId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Enrollment> query = session.createQuery(
                    "SELECT DISTINCT e FROM Enrollment e LEFT JOIN FETCH e.course LEFT JOIN FETCH e.student WHERE e.student.studentId = :studentId ORDER BY e.enrollmentDate DESC",
                    Enrollment.class);
            query.setParameter("studentId", studentId);
            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Get enrollments by course ID.
     *
     * @param courseId Course ID
     * @return List of enrollments for the course
     */
    public List<Enrollment> findByCourseId(Long courseId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Enrollment> query = session.createQuery(
                    "SELECT DISTINCT e FROM Enrollment e LEFT JOIN FETCH e.course LEFT JOIN FETCH e.student WHERE e.course.courseId = :courseId",
                    Enrollment.class);
            query.setParameter("courseId", courseId);
            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Check if student is already enrolled in a course.
     *
     * @param studentId Student ID
     * @param courseId Course ID
     * @return Enrollment if exists, null otherwise
     */
    public Enrollment findByStudentAndCourse(Long studentId, Long courseId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Enrollment> query = session.createQuery(
                    "SELECT e FROM Enrollment e LEFT JOIN FETCH e.course LEFT JOIN FETCH e.student WHERE e.student.studentId = :studentId AND e.course.courseId = :courseId",
                    Enrollment.class);
            query.setParameter("studentId", studentId);
            query.setParameter("courseId", courseId);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Update enrollment.
     *
     * @param enrollment Enrollment entity to update
     * @return Updated enrollment
     */
    public Enrollment update(Enrollment enrollment) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(enrollment);
            // Update course enrolled count when status changes
            Course course = enrollment.getCourse();
            if (course != null) {
                Query<Long> countQuery = session.createQuery(
                        "SELECT COUNT(*) FROM Enrollment WHERE course.courseId = :courseId AND status = 'ACTIVE'",
                        Long.class);
                countQuery.setParameter("courseId", course.getCourseId());
                Long count = countQuery.uniqueResult();
                course.setEnrolledCount(count != null ? count.intValue() : 0);
                session.update(course);
            }
            // Reload with associations to avoid lazy loading issues
            Query<Enrollment> query = session.createQuery(
                    "SELECT e FROM Enrollment e LEFT JOIN FETCH e.course LEFT JOIN FETCH e.student WHERE e.enrollmentId = :id",
                    Enrollment.class);
            query.setParameter("id", enrollment.getEnrollmentId());
            Enrollment updatedEnrollment = query.uniqueResult();
            transaction.commit();
            return updatedEnrollment != null ? updatedEnrollment : enrollment;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating enrollment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    /**
     * Delete enrollment by ID.
     *
     * @param id Enrollment ID
     */
    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Enrollment enrollment = session.get(Enrollment.class, id);
            if (enrollment != null) {
                Course course = enrollment.getCourse();
                session.delete(enrollment);
                // Update course enrolled count by querying actual count
                if (course != null) {
                    Query<Long> countQuery = session.createQuery(
                            "SELECT COUNT(*) FROM Enrollment WHERE course.courseId = :courseId AND status = 'ACTIVE'",
                            Long.class);
                    countQuery.setParameter("courseId", course.getCourseId());
                    Long count = countQuery.uniqueResult();
                    course.setEnrolledCount(count != null ? count.intValue() : 0);
                    session.update(course);
                }
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting enrollment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }
}


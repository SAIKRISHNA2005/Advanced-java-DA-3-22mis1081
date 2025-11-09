package com.example.onlinecourse.dao;

import com.example.onlinecourse.entity.Payment;
import com.example.onlinecourse.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object for Payment entity.
 * Provides CRUD operations for Payment management.
 */
public class PaymentDAO {
    private SessionFactory sessionFactory;

    public PaymentDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    /**
     * Save a new payment.
     *
     * @param payment Payment entity to save
     * @return Saved payment with generated ID
     */
    public Payment save(Payment payment) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(payment);
            // Flush to get the generated ID
            session.flush();
            // Reload with associations to avoid lazy loading issues
            Query<Payment> query = session.createQuery(
                    "SELECT p FROM Payment p LEFT JOIN FETCH p.course LEFT JOIN FETCH p.student WHERE p.paymentId = :id",
                    Payment.class);
            query.setParameter("id", payment.getPaymentId());
            Payment savedPayment = query.uniqueResult();
            transaction.commit();
            return savedPayment != null ? savedPayment : payment;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving payment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    /**
     * Find payment by ID.
     *
     * @param id Payment ID
     * @return Payment entity or null if not found
     */
    public Payment findById(Long id) {
        Session session = sessionFactory.openSession();
        try {
            Query<Payment> query = session.createQuery(
                    "SELECT p FROM Payment p LEFT JOIN FETCH p.course LEFT JOIN FETCH p.student WHERE p.paymentId = :id",
                    Payment.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Get all payments.
     *
     * @return List of all payments
     */
    public List<Payment> findAll() {
        Session session = sessionFactory.openSession();
        try {
            Query<Payment> query = session.createQuery(
                    "SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.course LEFT JOIN FETCH p.student ORDER BY p.paymentDate DESC",
                    Payment.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Get payments by student ID.
     *
     * @param studentId Student ID
     * @return List of payments for the student
     */
    public List<Payment> findByStudentId(Long studentId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Payment> query = session.createQuery(
                    "SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.course LEFT JOIN FETCH p.student WHERE p.student.studentId = :studentId ORDER BY p.paymentDate DESC",
                    Payment.class);
            query.setParameter("studentId", studentId);
            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Get payments by course ID.
     *
     * @param courseId Course ID
     * @return List of payments for the course
     */
    public List<Payment> findByCourseId(Long courseId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Payment> query = session.createQuery(
                    "SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.course LEFT JOIN FETCH p.student WHERE p.course.courseId = :courseId ORDER BY p.paymentDate DESC",
                    Payment.class);
            query.setParameter("courseId", courseId);
            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Update payment.
     *
     * @param payment Payment entity to update
     * @return Updated payment
     */
    public Payment update(Payment payment) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(payment);
            transaction.commit();
            return payment;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating payment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    /**
     * Delete payment by ID.
     *
     * @param id Payment ID
     */
    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Payment payment = session.get(Payment.class, id);
            if (payment != null) {
                session.delete(payment);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting payment: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }
}


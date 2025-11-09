package com.example.onlinecourse.dao;

import com.example.onlinecourse.entity.Student;
import com.example.onlinecourse.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object for Student entity.
 * Provides CRUD operations for Student management.
 */
public class StudentDAO {
    private SessionFactory sessionFactory;

    public StudentDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    /**
     * Save a new student.
     *
     * @param student Student entity to save
     * @return Saved student with generated ID
     */
    public Student save(Student student) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(student);
            transaction.commit();
            return student;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving student: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    /**
     * Find student by ID.
     *
     * @param id Student ID
     * @return Student entity or null if not found
     */
    public Student findById(Long id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Student.class, id);
        } finally {
            session.close();
        }
    }

    /**
     * Find student by email.
     *
     * @param email Student email
     * @return Student entity or null if not found
     */
    public Student findByEmail(String email) {
        Session session = sessionFactory.openSession();
        try {
            Query<Student> query = session.createQuery("FROM Student WHERE email = :email", Student.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }

    /**
     * Get all students.
     *
     * @return List of all students
     */
    public List<Student> findAll() {
        Session session = sessionFactory.openSession();
        try {
            Query<Student> query = session.createQuery("FROM Student", Student.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Update student.
     *
     * @param student Student entity to update
     * @return Updated student
     */
    public Student update(Student student) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(student);
            transaction.commit();
            return student;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating student: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    /**
     * Delete student by ID.
     *
     * @param id Student ID
     */
    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Student student = session.get(Student.class, id);
            if (student != null) {
                session.delete(student);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting student: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    /**
     * Check if email exists.
     *
     * @param email Email to check
     * @return true if email exists
     */
    public boolean emailExists(String email) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Student WHERE email = :email", Long.class);
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        } finally {
            session.close();
        }
    }
}


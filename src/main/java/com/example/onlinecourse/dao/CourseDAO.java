package com.example.onlinecourse.dao;

import com.example.onlinecourse.entity.Course;
import com.example.onlinecourse.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object for Course entity.
 * Provides CRUD operations for Course management.
 */
public class CourseDAO {
    private SessionFactory sessionFactory;

    public CourseDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    /**
     * Save a new course.
     *
     * @param course Course entity to save
     * @return Saved course with generated ID
     */
    public Course save(Course course) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(course);
            transaction.commit();
            return course;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving course: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    /**
     * Find course by ID.
     *
     * @param id Course ID
     * @return Course entity or null if not found
     */
    public Course findById(Long id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Course.class, id);
        } finally {
            session.close();
        }
    }

    /**
     * Get all courses.
     *
     * @return List of all courses
     */
    public List<Course> findAll() {
        Session session = sessionFactory.openSession();
        try {
            Query<Course> query = session.createQuery("FROM Course ORDER BY courseName", Course.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Get available courses (not full).
     *
     * @return List of available courses
     */
    public List<Course> findAvailableCourses() {
        Session session = sessionFactory.openSession();
        try {
            Query<Course> query = session.createQuery(
                    "FROM Course WHERE enrolledCount < capacity ORDER BY courseName", Course.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    /**
     * Update course.
     *
     * @param course Course entity to update
     * @return Updated course
     */
    public Course update(Course course) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(course);
            transaction.commit();
            return course;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating course: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    /**
     * Delete course by ID.
     *
     * @param id Course ID
     */
    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Course course = session.get(Course.class, id);
            if (course != null) {
                session.delete(course);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting course: " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    /**
     * Search courses by name or instructor.
     *
     * @param searchTerm Search term
     * @return List of matching courses
     */
    public List<Course> searchCourses(String searchTerm) {
        Session session = sessionFactory.openSession();
        try {
            Query<Course> query = session.createQuery(
                    "FROM Course WHERE courseName LIKE :term OR instructor LIKE :term ORDER BY courseName",
                    Course.class);
            query.setParameter("term", "%" + searchTerm + "%");
            return query.list();
        } finally {
            session.close();
        }
    }
}


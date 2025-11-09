package com.example.onlinecourse.service;

import com.example.onlinecourse.dao.CourseDAO;
import com.example.onlinecourse.entity.Course;

import java.util.List;

/**
 * Service layer for Course business logic.
 * Provides business operations on top of CourseDAO.
 */
public class CourseService {
    private CourseDAO courseDAO;

    public CourseService() {
        this.courseDAO = new CourseDAO();
    }

    /**
     * Create a new course.
     *
     * @param course Course to create
     * @return Created course
     */
    public Course createCourse(Course course) {
        if (course.getEnrolledCount() == null) {
            course.setEnrolledCount(0);
        }
        return courseDAO.save(course);
    }

    /**
     * Get course by ID.
     *
     * @param id Course ID
     * @return Course entity
     */
    public Course getCourseById(Long id) {
        return courseDAO.findById(id);
    }

    /**
     * Get all courses.
     *
     * @return List of all courses
     */
    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    /**
     * Get available courses (not full).
     *
     * @return List of available courses
     */
    public List<Course> getAvailableCourses() {
        return courseDAO.findAvailableCourses();
    }

    /**
     * Update course.
     *
     * @param course Course to update
     * @return Updated course
     */
    public Course updateCourse(Course course) {
        return courseDAO.update(course);
    }

    /**
     * Delete course.
     *
     * @param id Course ID
     */
    public void deleteCourse(Long id) {
        courseDAO.delete(id);
    }

    /**
     * Search courses by name or instructor.
     *
     * @param searchTerm Search term
     * @return List of matching courses
     */
    public List<Course> searchCourses(String searchTerm) {
        return courseDAO.searchCourses(searchTerm);
    }
}


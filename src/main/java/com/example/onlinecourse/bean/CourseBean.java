package com.example.onlinecourse.bean;

import com.example.onlinecourse.entity.Course;
import com.example.onlinecourse.service.CourseService;
import com.example.onlinecourse.util.DateFormatter;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * JSF Managed Bean for Course operations.
 * Handles course listing, details, and search.
 */
@Named("courseBean")
@jakarta.enterprise.context.SessionScoped
public class CourseBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private CourseService courseService;
    private List<Course> courses;
    private Course selectedCourse;
    private String searchTerm;
    private Long courseId;

    // For course creation (admin functionality)
    private String courseName;
    private String description;
    private String instructor;
    private Date startDate;
    private Date endDate;
    private BigDecimal fee;
    private Integer capacity;

    public CourseBean() {
        this.courseService = new CourseService();
        loadCourses();
    }

    /**
     * Load course from URL parameter.
     * Called before rendering course details page.
     */
    public void loadCourseFromParam(jakarta.faces.event.ComponentSystemEvent event) {
        // Get courseId from request parameters if not set
        if (courseId == null) {
            String courseIdParam = FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestParameterMap().get("courseId");
            if (courseIdParam != null && !courseIdParam.isEmpty()) {
                try {
                    courseId = Long.parseLong(courseIdParam);
                } catch (NumberFormatException e) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid course ID: " + courseIdParam));
                    return;
                }
            }
        }
        
        if (courseId != null && (selectedCourse == null || !selectedCourse.getCourseId().equals(courseId))) {
            selectedCourse = courseService.getCourseById(courseId);
            if (selectedCourse == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Course not found with ID: " + courseId));
            }
        } else if (courseId == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Course ID is required"));
        }
    }

    /**
     * Load all courses.
     */
    public void loadCourses() {
        courses = courseService.getAllCourses();
    }

    /**
     * Load available courses only.
     */
    public void loadAvailableCourses() {
        courses = courseService.getAvailableCourses();
    }

    /**
     * Search courses.
     */
    public void searchCourses() {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            courses = courseService.searchCourses(searchTerm);
        } else {
            loadCourses();
        }
    }

    /**
     * Get total number of courses.
     *
     * @return Total course count
     */
    public int getTotalCoursesCount() {
        if (courses == null) {
            loadCourses();
        }
        return courses != null ? courses.size() : 0;
    }

    /**
     * Get number of available courses.
     *
     * @return Available course count
     */
    public int getAvailableCoursesCount() {
        if (courses == null) {
            loadCourses();
        }
        if (courses == null) {
            return 0;
        }
        return (int) courses.stream()
                .filter(c -> c.getEnrolledCount() < c.getCapacity())
                .count();
    }

    /**
     * Get course details by ID.
     * This method is called when clicking "View Details" button.
     *
     * @param courseId Course ID
     * @return Navigation outcome
     */
    public String viewCourseDetails(Long courseId) {
        if (courseId == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Course ID is required"));
            return "courseList?faces-redirect=true";
        }
        
        this.courseId = courseId;
        selectedCourse = courseService.getCourseById(courseId);
        if (selectedCourse == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Course not found with ID: " + courseId));
            return "courseList?faces-redirect=true";
        }
        // Navigate to course details page with courseId parameter
        return "courseDetails?faces-redirect=true&courseId=" + courseId;
    }

    /**
     * Create a new course (admin functionality).
     *
     * @return Navigation outcome
     */
    public String createCourse() {
        try {
            Course course = new Course();
            course.setCourseName(courseName);
            course.setDescription(description);
            course.setInstructor(instructor);
            course.setStartDate(startDate);
            course.setEndDate(endDate);
            course.setFee(fee);
            course.setCapacity(capacity != null ? capacity : 50);
            course.setEnrolledCount(0);

            courseService.createCourse(course);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Course created successfully!"));

            resetForm();
            loadCourses();
            return "courseList?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }

    /**
     * Edit a course.
     * Gets courseId from request parameters.
     *
     * @return Navigation outcome
     */
    public String editCourse() {
        String courseIdParam = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("courseId");
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                Long id = Long.parseLong(courseIdParam);
                Course course = courseService.getCourseById(id);
                if (course != null) {
                    this.courseId = id;
                    courseName = course.getCourseName();
                    description = course.getDescription();
                    instructor = course.getInstructor();
                    startDate = course.getStartDate();
                    endDate = course.getEndDate();
                    fee = course.getFee();
                    capacity = course.getCapacity();
                    return "admin";
                }
            } catch (NumberFormatException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid course ID"));
            }
        }
        return null;
    }

    /**
     * Update course.
     *
     * @return Navigation outcome
     */
    public String updateCourse() {
        try {
            if (courseId == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Course ID is required"));
                return null;
            }

            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Course not found"));
                return null;
            }

            course.setCourseName(courseName);
            course.setDescription(description);
            course.setInstructor(instructor);
            course.setStartDate(startDate);
            course.setEndDate(endDate);
            course.setFee(fee);
            course.setCapacity(capacity);

            courseService.updateCourse(course);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Course updated successfully!"));

            resetForm();
            loadCourses();
            courseId = null;
            return "admin?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }

    /**
     * Delete a course.
     * Gets courseId from request parameters.
     *
     * @return Navigation outcome
     */
    public String deleteCourse() {
        try {
            String courseIdParam = FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestParameterMap().get("courseId");
            if (courseIdParam != null && !courseIdParam.isEmpty()) {
                Long id = Long.parseLong(courseIdParam);
                courseService.deleteCourse(id);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Course deleted successfully!"));
                loadCourses();
                return "admin?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Course ID is required"));
                return null;
            }
        } catch (NumberFormatException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid course ID"));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }

    /**
     * Cancel edit mode.
     *
     * @return Navigation outcome
     */
    public String cancelEdit() {
        resetForm();
        return "admin?faces-redirect=true";
    }

    /**
     * Reset form.
     */
    private void resetForm() {
        courseId = null;
        courseName = "";
        description = "";
        instructor = "";
        startDate = null;
        endDate = null;
        fee = null;
        capacity = null;
    }

    // Getters and Setters
    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public Course getSelectedCourse() {
        return selectedCourse;
    }

    public void setSelectedCourse(Course selectedCourse) {
        this.selectedCourse = selectedCourse;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * Format date for display.
     *
     * @param date Date to format
     * @return Formatted date string
     */
    public String formatDate(Date date) {
        return DateFormatter.formatDate(date);
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}


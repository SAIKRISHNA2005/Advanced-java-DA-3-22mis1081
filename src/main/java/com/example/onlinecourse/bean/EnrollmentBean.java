package com.example.onlinecourse.bean;

import com.example.onlinecourse.entity.Enrollment;
import com.example.onlinecourse.entity.Student;
import com.example.onlinecourse.service.EnrollmentService;
import com.example.onlinecourse.service.StudentService;
import com.example.onlinecourse.util.DateFormatter;
import com.example.onlinecourse.bean.CourseBean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * JSF Managed Bean for Enrollment operations.
 * Handles course enrollment and cancellation.
 */
@Named("enrollmentBean")
@ViewScoped
public class EnrollmentBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private EnrollmentService enrollmentService;
    private StudentService studentService;
    private List<Enrollment> enrollments;
    private List<Enrollment> allEnrollments;
    private Long selectedCourseId;
    private Long selectedEnrollmentId;

    public EnrollmentBean() {
        this.enrollmentService = new EnrollmentService();
        this.studentService = new StudentService();
    }

    /**
     * Get current logged-in student.
     *
     * @return Student entity
     */
    private Student getCurrentStudent() {
        try {
            StudentBean studentBean = (StudentBean) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("studentBean");
            if (studentBean == null) {
                // Try to get from CDI context
                studentBean = FacesContext.getCurrentInstance().getApplication()
                        .evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{studentBean}", StudentBean.class);
            }
            return studentBean != null ? studentBean.getCurrentStudent() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Load enrollments for current student.
     */
    public void loadEnrollments() {
        Student student = getCurrentStudent();
        if (student != null) {
            enrollments = enrollmentService.getEnrollmentsByStudent(student.getStudentId());
        }
    }

    /**
     * Enroll in a course.
     * Gets courseId from request parameters or from selectedCourseId.
     *
     * @return Navigation outcome
     */
    public String enrollInCourse() {
        try {
            Student student = getCurrentStudent();
            if (student == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please login first. Redirecting to login page..."));
                return "home?faces-redirect=true";
            }

            // Get courseId from request parameters
            String courseIdParam = FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestParameterMap().get("courseId");
            
            // If not in request, try to get from CourseBean
            Long courseId = null;
            if (courseIdParam != null && !courseIdParam.isEmpty()) {
                courseId = Long.parseLong(courseIdParam);
            } else {
                // Try to get from CourseBean
                CourseBean courseBean = (CourseBean) FacesContext.getCurrentInstance()
                        .getExternalContext().getSessionMap().get("courseBean");
                if (courseBean != null && courseBean.getSelectedCourse() != null) {
                    courseId = courseBean.getSelectedCourse().getCourseId();
                }
            }
            
            if (courseId == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Course ID is required"));
                return null;
            }

            Enrollment enrollment = enrollmentService.enrollStudent(student.getStudentId(), courseId);
            
            // Get course name for success message
            com.example.onlinecourse.service.CourseService courseService = 
                new com.example.onlinecourse.service.CourseService();
            com.example.onlinecourse.entity.Course course = courseService.getCourseById(courseId);
            String courseName = course != null ? course.getCourseName() : "the course";
            
            // Store success message in session for toast notification
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put("enrollmentSuccess", true);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put("enrollmentMessage", "You have successfully enrolled in " + courseName + "! Proceed to payment. Enjoy learning! 🎓");

            // Reload enrollments to update the list
            enrollments = null;

            // Redirect to payment page with courseId
            return "payment?courseId=" + courseId + "&faces-redirect=true";
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
     * Cancel an enrollment.
     * Gets enrollmentId from request parameters.
     *
     * @return Navigation outcome
     */
    public String cancelEnrollment() {
        try {
            // Get enrollmentId from request parameters
            String enrollmentIdParam = FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestParameterMap().get("enrollmentId");
            
            if (enrollmentIdParam == null || enrollmentIdParam.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Enrollment ID is required"));
                return null;
            }

            Long enrollmentId = Long.parseLong(enrollmentIdParam);
            enrollmentService.cancelEnrollment(enrollmentId);
            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Enrollment cancelled successfully!"));

            // Reload enrollments
            enrollments = null;
            return null; // Stay on same page and reload
        } catch (NumberFormatException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid enrollment ID"));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }

    /**
     * Check if student is enrolled in a course.
     *
     * @param courseId Course ID
     * @return true if enrolled
     */
    public boolean isEnrolled(Long courseId) {
        Student student = getCurrentStudent();
        if (student == null) {
            return false;
        }
        return enrollmentService.isEnrolled(student.getStudentId(), courseId);
    }

    /**
     * Get total number of active enrollments.
     *
     * @return Count of active enrollments
     */
    public int getActiveEnrollmentsCount() {
        if (enrollments == null) {
            loadEnrollments();
        }
        if (enrollments == null) {
            return 0;
        }
        return (int) enrollments.stream()
                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.ACTIVE)
                .count();
    }

    // Getters and Setters
    public List<Enrollment> getEnrollments() {
        if (enrollments == null) {
            loadEnrollments();
        }
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public Long getSelectedCourseId() {
        return selectedCourseId;
    }

    public void setSelectedCourseId(Long selectedCourseId) {
        this.selectedCourseId = selectedCourseId;
    }

    public Long getSelectedEnrollmentId() {
        return selectedEnrollmentId;
    }

    public void setSelectedEnrollmentId(Long selectedEnrollmentId) {
        this.selectedEnrollmentId = selectedEnrollmentId;
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

    /**
     * Format date-time for display.
     *
     * @param date Date to format
     * @return Formatted date-time string
     */
    public String formatDateTime(Date date) {
        return DateFormatter.formatDateTime(date);
    }

    /**
     * View all enrollments (admin function).
     *
     * @return Navigation outcome
     */
    public String viewAllEnrollments() {
        allEnrollments = enrollmentService.getAllEnrollments();
        return "admin";
    }

    public List<Enrollment> getAllEnrollments() {
        if (allEnrollments == null) {
            allEnrollments = enrollmentService.getAllEnrollments();
        }
        return allEnrollments;
    }

    public void setAllEnrollments(List<Enrollment> allEnrollments) {
        this.allEnrollments = allEnrollments;
    }
}


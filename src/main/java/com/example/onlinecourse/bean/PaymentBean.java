package com.example.onlinecourse.bean;

import com.example.onlinecourse.dao.PaymentDAO;
import com.example.onlinecourse.entity.Course;
import com.example.onlinecourse.entity.Payment;
import com.example.onlinecourse.entity.Student;
import com.example.onlinecourse.service.CourseService;
import com.example.onlinecourse.service.StudentService;
import com.example.onlinecourse.util.DateFormatter;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * JSF Managed Bean for Payment operations.
 * Handles payment processing and payment history.
 */
@Named("paymentBean")
@ViewScoped
public class PaymentBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private PaymentDAO paymentDAO;
    private CourseService courseService;
    private StudentService studentService;
    private List<Payment> payments;
    private Payment currentPayment;
    private Course selectedCourse;
    private Long courseId;
    private Payment.PaymentMethod paymentMethod;
    private String transactionId;
    private Long paymentId;
    private Payment confirmedPayment;

    public PaymentBean() {
        this.paymentDAO = new PaymentDAO();
        this.courseService = new CourseService();
        this.studentService = new StudentService();
    }

    /**
     * Initialize payment for a course.
     * Called before rendering the payment page.
     */
    public void init(ComponentSystemEvent event) {
        // Get courseId from request parameters
        String courseIdParam = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("courseId");
        
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                courseId = Long.parseLong(courseIdParam);
                selectedCourse = courseService.getCourseById(courseId);
                if (selectedCourse != null) {
                    currentPayment = new Payment();
                    currentPayment.setCourse(selectedCourse);
                    currentPayment.setAmount(selectedCourse.getFee());
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Course not found with ID: " + courseId));
                }
            } catch (NumberFormatException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid course ID: " + courseIdParam));
            }
        } else {
            // If no courseId in URL, try to get from CourseBean
            CourseBean courseBean = (CourseBean) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("courseBean");
            if (courseBean != null && courseBean.getSelectedCourse() != null) {
                selectedCourse = courseBean.getSelectedCourse();
                courseId = selectedCourse.getCourseId();
                currentPayment = new Payment();
                currentPayment.setCourse(selectedCourse);
                currentPayment.setAmount(selectedCourse.getFee());
            }
        }
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
     * Load payments for current student.
     */
    public void loadPayments() {
        Student student = getCurrentStudent();
        if (student != null) {
            payments = paymentDAO.findByStudentId(student.getStudentId());
        }
    }

    /**
     * Process payment.
     *
     * @return Navigation outcome
     */
    public String processPayment() {
        try {
            Student student = getCurrentStudent();
            if (student == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please login first"));
                return "home?faces-redirect=true";
            }

            // Ensure selectedCourse is loaded
            Course course = getSelectedCourse();
            if (course == null) {
                // Try to get courseId from request parameters
                String courseIdParam = FacesContext.getCurrentInstance().getExternalContext()
                        .getRequestParameterMap().get("courseId");
                if (courseIdParam != null && !courseIdParam.isEmpty()) {
                    try {
                        courseId = Long.parseLong(courseIdParam);
                        course = courseService.getCourseById(courseId);
                    } catch (NumberFormatException e) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid course ID"));
                        return null;
                    }
                }
            }

            if (course == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Course not found. Please select a course."));
                return "courseList?faces-redirect=true";
            }

            if (paymentMethod == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please select a payment method"));
                return null;
            }

            Payment payment = new Payment();
            payment.setStudent(student);
            payment.setCourse(course);
            payment.setAmount(course.getFee());
            payment.setPaymentMethod(paymentMethod);
            payment.setTransactionId(transactionId != null && !transactionId.isEmpty() ?
                    transactionId : "TXN-" + System.currentTimeMillis());
            payment.setStatus(Payment.PaymentStatus.COMPLETED);

            Payment savedPayment = paymentDAO.save(payment);

            // Reload payments
            payments = null;

            // Redirect to payment confirmation page with payment ID
            return "paymentConfirmation?paymentId=" + savedPayment.getPaymentId() + "&faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }

    /**
     * Handle payment method change.
     *
     * @param event Value change event
     */
    public void onPaymentMethodChange(jakarta.faces.event.ValueChangeEvent event) {
        paymentMethod = (Payment.PaymentMethod) event.getNewValue();
    }

    // Getters and Setters
    public List<Payment> getPayments() {
        if (payments == null) {
            loadPayments();
        }
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public Payment getCurrentPayment() {
        return currentPayment;
    }

    public void setCurrentPayment(Payment currentPayment) {
        this.currentPayment = currentPayment;
    }

    public Course getSelectedCourse() {
        if (selectedCourse == null) {
            // Try to get courseId from request parameters first
            String courseIdParam = FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestParameterMap().get("courseId");
            
            if (courseIdParam != null && !courseIdParam.isEmpty()) {
                try {
                    courseId = Long.parseLong(courseIdParam);
                    selectedCourse = courseService.getCourseById(courseId);
                } catch (NumberFormatException e) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid course ID"));
                }
            } else if (courseId != null) {
                selectedCourse = courseService.getCourseById(courseId);
            }
        }
        return selectedCourse;
    }

    public void setSelectedCourse(Course selectedCourse) {
        this.selectedCourse = selectedCourse;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Payment.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Payment.PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Payment.PaymentMethod[] getPaymentMethods() {
        return Payment.PaymentMethod.values();
    }

    /**
     * Calculate total amount spent by student.
     * Only counts payments for active enrollments.
     *
     * @return Total amount as BigDecimal
     */
    public java.math.BigDecimal getTotalSpent() {
        Student student = getCurrentStudent();
        if (student == null) {
            return java.math.BigDecimal.ZERO;
        }
        
        if (payments == null) {
            loadPayments();
        }
        if (payments == null || payments.isEmpty()) {
            return java.math.BigDecimal.ZERO;
        }
        
        // Get active enrollments for the student
        com.example.onlinecourse.service.EnrollmentService enrollmentService = 
            new com.example.onlinecourse.service.EnrollmentService();
        java.util.List<com.example.onlinecourse.entity.Enrollment> activeEnrollments = 
            enrollmentService.getEnrollmentsByStudent(student.getStudentId());
        
        // Create a set of course IDs for active enrollments
        java.util.Set<Long> activeCourseIds = activeEnrollments.stream()
            .filter(e -> e.getStatus() == com.example.onlinecourse.entity.Enrollment.EnrollmentStatus.ACTIVE)
            .map(e -> e.getCourse().getCourseId())
            .collect(java.util.stream.Collectors.toSet());
        
        // Sum payments only for active enrollments
        return payments.stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED 
                          && activeCourseIds.contains(p.getCourse().getCourseId()))
                .map(Payment::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * Get total number of payments.
     *
     * @return Total payment count
     */
    public int getTotalPaymentsCount() {
        if (payments == null) {
            loadPayments();
        }
        return payments != null ? payments.size() : 0;
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
     * Load payment confirmation details.
     * Called before rendering payment confirmation page.
     */
    public void loadPaymentConfirmation(jakarta.faces.event.ComponentSystemEvent event) {
        // Get paymentId from request parameters
        String paymentIdParam = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("paymentId");
        
        if (paymentIdParam != null && !paymentIdParam.isEmpty()) {
            try {
                paymentId = Long.parseLong(paymentIdParam);
                confirmedPayment = paymentDAO.findById(paymentId);
                if (confirmedPayment == null) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Payment not found with ID: " + paymentId));
                }
            } catch (NumberFormatException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid payment ID: " + paymentIdParam));
            }
        } else {
            // Try to get the most recent payment for current student
            Student student = getCurrentStudent();
            if (student != null) {
                List<Payment> studentPayments = paymentDAO.findByStudentId(student.getStudentId());
                if (studentPayments != null && !studentPayments.isEmpty()) {
                    confirmedPayment = studentPayments.get(studentPayments.size() - 1);
                }
            }
        }
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Payment getConfirmedPayment() {
        return confirmedPayment;
    }

    public void setConfirmedPayment(Payment confirmedPayment) {
        this.confirmedPayment = confirmedPayment;
    }
}


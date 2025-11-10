package com.example.onlinecourse.bean;

import com.example.onlinecourse.entity.Student;
import com.example.onlinecourse.service.StudentService;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JSF Managed Bean for Student operations.
 * Handles student registration and authentication.
 */
@Named("studentBean")
@SessionScoped
public class StudentBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private StudentService studentService;
    private Student currentStudent;
    private Student newStudent;
    private List<Student> allStudents;

    // Registration form fields
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String address;

    // Login form fields
    private String loginEmail;
    private String loginPassword;

    public StudentBean() {
        this.studentService = new StudentService();
        this.newStudent = new Student();
    }

    /**
     * Register a new student.
     *
     * @return Navigation outcome
     */
    public String register() {
        try {
            newStudent.setFirstName(firstName);
            newStudent.setLastName(lastName);
            newStudent.setEmail(email);
            newStudent.setPassword(password);
            newStudent.setPhone(phone);
            newStudent.setAddress(address);

            Student registered = studentService.registerStudent(newStudent);
            currentStudent = registered;

            // Store success message in session for toast notification
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put("registrationSuccess", true);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put("registrationMessage", "Welcome, " + registered.getFirstName() + "! Your account has been created successfully. Enjoy learning! 🎉");

            // Reset form
            resetRegistrationForm();

            return "dashboard?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }

    /**
     * Login student.
     *
     * @return Navigation outcome
     */
    public String login() {
        try {
            Student student = studentService.authenticate(loginEmail, loginPassword);
            if (student != null) {
                currentStudent = student;
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Login successful!"));
                return "dashboard?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid email or password"));
                return null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }

    /**
     * Logout student.
     *
     * @return Navigation outcome
     */
    public String logout() {
        currentStudent = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        allStudents = null;
        return "home?faces-redirect=true";
    }

    /**
     * Check if student is logged in.
     *
     * @return true if logged in
     */
    public boolean isLoggedIn() {
        return currentStudent != null;
    }

    /**
     * Check if current user is admin.
     * Admin is identified by email: admin@course.com
     *
     * @return true if current user is admin
     */
    public boolean isAdmin() {
        return currentStudent != null && "admin@course.com".equalsIgnoreCase(currentStudent.getEmail());
    }

    /**
     * Clear toast notification session attributes.
     * Called after toast notification is displayed via AJAX.
     * Returns null to stay on the same page.
     */
    public String clearToastNotifications() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("registrationSuccess");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("registrationMessage");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("enrollmentSuccess");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("enrollmentMessage");
        return null; // Stay on same page
    }

    /**
     * Reset registration form.
     */
    private void resetRegistrationForm() {
        firstName = "";
        lastName = "";
        email = "";
        password = "";
        phone = "";
        address = "";
        newStudent = new Student();
    }

    // Getters and Setters
    public Student getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(Student currentStudent) {
        this.currentStudent = currentStudent;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    /**
     * Update student profile.
     *
     * @return Navigation outcome
     */
    public String updateProfile() {
        try {
            if (currentStudent == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please login first"));
                return null;
            }

            currentStudent.setFirstName(firstName);
            currentStudent.setLastName(lastName);
            currentStudent.setPhone(phone);
            currentStudent.setAddress(address);

            studentService.updateStudent(currentStudent);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Profile updated successfully!"));

            return null; // Stay on same page
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }

    /**
     * Load profile data for editing.
     */
    public void loadProfileData() {
        if (currentStudent != null) {
            firstName = currentStudent.getFirstName();
            lastName = currentStudent.getLastName();
            phone = currentStudent.getPhone();
            address = currentStudent.getAddress();
        }
    }

    /**
     * Load all students for admin management.
     */
    public void loadAllStudents() {
        if (isAdmin()) {
            allStudents = studentService.getAllStudents();
        }
    }

    /**
     * JSF event wrapper for loading students on view render.
     *
     * @param event component system event
     */
    public void loadAllStudents(ComponentSystemEvent event) {
        loadAllStudents();
    }

    /**
     * Get list of students excluding admin accounts for management.
     *
     * @return list of non-admin students
     */
    public List<Student> getManagedStudents() {
        if (!isAdmin()) {
            return List.of();
        }
        if (allStudents == null) {
            loadAllStudents();
        }
        if (allStudents == null) {
            return List.of();
        }
        return allStudents.stream()
                .filter(student -> student.getEmail() != null && !isAdminEmail(student.getEmail()))
                .collect(Collectors.toList());
    }

    /**
     * Delete student (admin operation).
     *
     * @return navigation outcome
     */
    public String deleteStudent() {
        if (!isAdmin()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Access denied."));
            return null;
        }

        String studentIdParam = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("studentId");
        if (studentIdParam == null || studentIdParam.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Student ID is required."));
            return null;
        }

        try {
            Long studentId = Long.parseLong(studentIdParam);
            Student student = studentService.getStudentById(studentId);
            if (student == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Student not found."));
                return null;
            }
            if (student.getEmail() != null && isAdminEmail(student.getEmail())) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", "Cannot delete administrator account."));
                return null;
            }

            studentService.deleteStudent(studentId);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Student deleted successfully."));
            loadAllStudents();
            return "manageUsers?faces-redirect=true";
        } catch (NumberFormatException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid student ID."));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }

    /**
     * Helper to check admin email.
     *
     * @param email email to verify
     * @return true if email belongs to admin
     */
    private boolean isAdminEmail(String email) {
        return "admin@course.com".equalsIgnoreCase(email);
    }
}


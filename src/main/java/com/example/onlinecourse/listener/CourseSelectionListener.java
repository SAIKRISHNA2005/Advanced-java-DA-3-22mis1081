package com.example.onlinecourse.listener;

import com.example.onlinecourse.bean.CourseBean;
import com.example.onlinecourse.entity.Course;
import com.example.onlinecourse.service.CourseService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;

/**
 * JSF ActionListener for handling course selection.
 * Demonstrates the use of ActionListener for user interactions.
 */
public class CourseSelectionListener implements ActionListener {

    @Override
    public void processAction(ActionEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        // Get courseId from request parameters
        String courseIdParam = facesContext.getExternalContext()
                .getRequestParameterMap().get("courseId");
        
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                Long courseId = Long.parseLong(courseIdParam);
                CourseService courseService = new CourseService();
                Course course = courseService.getCourseById(courseId);
                
                if (course != null) {
                    // Set selected course in CourseBean
                    CourseBean courseBean = (CourseBean) facesContext.getExternalContext()
                            .getSessionMap().get("courseBean");
                    if (courseBean != null) {
                        courseBean.setSelectedCourse(course);
                        courseBean.setCourseId(courseId);
                    }
                } else {
                    facesContext.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                    "Error", "Course not found"));
                }
            } catch (NumberFormatException e) {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                                "Error", "Invalid course ID"));
            }
        }
    }
}


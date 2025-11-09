package com.example.onlinecourse.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * JSF Validator for password strength.
 * Validates that password meets strength requirements:
 * - At least 8 characters
 * - Contains uppercase letter
 * - Contains lowercase letter
 * - Contains digit
 * - Contains special character
 */
@FacesValidator("passwordStrengthValidator")
public class PasswordStrengthValidator implements Validator<String> {

    private static final String PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Override
    public void validate(FacesContext context, UIComponent component, String value) 
            throws ValidatorException {
        
        if (value == null || value.isEmpty()) {
            return; // Let required validator handle empty values
        }

        if (!value.matches(PASSWORD_PATTERN)) {
            String message = "Password must be at least 8 characters with uppercase, " +
                           "lowercase, number, and special character (@$!%*?&)";
            throw new ValidatorException(
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Password", message));
        }
    }
}


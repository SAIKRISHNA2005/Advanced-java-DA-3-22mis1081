package com.example.onlinecourse.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * JSF Converter for Date objects.
 * Converts between String and Date for JSF input components.
 */
@FacesConverter("dateConverter")
public class DateConverter implements Converter<Date> {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

    @Override
    public Date getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            synchronized (DATE_FORMAT) {
                return DATE_FORMAT.parse(value);
            }
        } catch (ParseException e) {
            throw new ConverterException("Invalid date format. Please use YYYY-MM-DD format.", e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Date value) {
        if (value == null) {
            return "";
        }

        try {
            synchronized (DATE_FORMAT) {
                return DATE_FORMAT.format(value);
            }
        } catch (Exception e) {
            throw new ConverterException("Error converting date to string.", e);
        }
    }
}


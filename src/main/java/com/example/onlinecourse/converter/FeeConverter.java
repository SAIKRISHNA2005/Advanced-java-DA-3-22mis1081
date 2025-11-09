package com.example.onlinecourse.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * JSF Converter for BigDecimal fee values.
 * Converts between String and BigDecimal for JSF input components.
 */
@FacesConverter("feeConverter")
public class FeeConverter implements Converter<BigDecimal> {

    @Override
    public BigDecimal getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            // Remove currency symbols and whitespace
            String cleanedValue = value.replaceAll("[₹$,\\s]", "");
            return new BigDecimal(cleanedValue);
        } catch (NumberFormatException e) {
            throw new ConverterException("Invalid fee format. Please enter a valid number.", e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, BigDecimal value) {
        if (value == null) {
            return "";
        }

        try {
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
            return formatter.format(value);
        } catch (Exception e) {
            throw new ConverterException("Error converting fee to string.", e);
        }
    }
}


package com.example.login_shared_pref.utils;

import android.util.Patterns;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 * Provides methods to validate email, password, and other user inputs
 */
public class ValidationUtils {

    // Password requirements
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 20;

    // Name requirements
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 50;

    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.length() >= MIN_PASSWORD_LENGTH &&
                password.length() <= MAX_PASSWORD_LENGTH;
    }

    /**
     * Validate full name
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String trimmedName = name.trim();
        return trimmedName.length() >= MIN_NAME_LENGTH &&
                trimmedName.length() <= MAX_NAME_LENGTH &&
                trimmedName.matches("^[a-zA-Z\\s]+$"); // Only letters and spaces
    }

    /**
     * Check if passwords match
     */
    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    /**
     * Validate if string is not empty
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Get email validation error message
     */
    public static String getEmailError(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!isValidEmail(email)) {
            return "Please enter a valid email address";
        }
        return null;
    }

    /**
     * Get password validation error message
     */
    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters";
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return "Password must be less than " + MAX_PASSWORD_LENGTH + " characters";
        }
        return null;
    }

    /**
     * Get name validation error message
     */
    public static String getNameError(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Full name is required";
        }

        String trimmedName = name.trim();
        if (trimmedName.length() < MIN_NAME_LENGTH) {
            return "Name must be at least " + MIN_NAME_LENGTH + " characters";
        }
        if (trimmedName.length() > MAX_NAME_LENGTH) {
            return "Name must be less than " + MAX_NAME_LENGTH + " characters";
        }
        if (!trimmedName.matches("^[a-zA-Z\\s]+$")) {
            return "Name can only contain letters and spaces";
        }
        return null;
    }

    /**
     * Get confirm password validation error message
     */
    public static String getConfirmPasswordError(String password, String confirmPassword) {
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return "Please confirm your password";
        }
        if (!doPasswordsMatch(password, confirmPassword)) {
            return "Passwords do not match";
        }
        return null;
    }

    /**
     * Validate all registration fields
     */
    public static ValidationResult validateRegistration(String name, String email,
                                                        String password, String confirmPassword) {
        ValidationResult result = new ValidationResult();

        // Validate name
        String nameError = getNameError(name);
        if (nameError != null) {
            result.setNameError(nameError);
            result.setValid(false);
        }

        // Validate email
        String emailError = getEmailError(email);
        if (emailError != null) {
            result.setEmailError(emailError);
            result.setValid(false);
        }

        // Validate password
        String passwordError = getPasswordError(password);
        if (passwordError != null) {
            result.setPasswordError(passwordError);
            result.setValid(false);
        }

        // Validate confirm password
        String confirmPasswordError = getConfirmPasswordError(password, confirmPassword);
        if (confirmPasswordError != null) {
            result.setConfirmPasswordError(confirmPasswordError);
            result.setValid(false);
        }

        return result;
    }

    /**
     * Validate login fields
     */
    public static ValidationResult validateLogin(String email, String password) {
        ValidationResult result = new ValidationResult();

        // Validate email
        String emailError = getEmailError(email);
        if (emailError != null) {
            result.setEmailError(emailError);
            result.setValid(false);
        }

        // Validate password
        if (password == null || password.isEmpty()) {
            result.setPasswordError("Password is required");
            result.setValid(false);
        }

        return result;
    }

    /**
     * Inner class to hold validation results
     */
    public static class ValidationResult {
        private boolean isValid = true;
        private String nameError;
        private String emailError;
        private String passwordError;
        private String confirmPasswordError;

        // Getters and setters
        public boolean isValid() { return isValid; }
        public void setValid(boolean valid) { isValid = valid; }

        public String getNameError() { return nameError; }
        public void setNameError(String nameError) { this.nameError = nameError; }

        public String getEmailError() { return emailError; }
        public void setEmailError(String emailError) { this.emailError = emailError; }

        public String getPasswordError() { return passwordError; }
        public void setPasswordError(String passwordError) { this.passwordError = passwordError; }

        public String getConfirmPasswordError() { return confirmPasswordError; }
        public void setConfirmPasswordError(String confirmPasswordError) {
            this.confirmPasswordError = confirmPasswordError;
        }
    }
}
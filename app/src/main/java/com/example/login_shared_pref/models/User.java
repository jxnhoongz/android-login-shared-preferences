package com.example.login_shared_pref.models;

/**
 * User data model
 * Represents a user with basic information
 */
public class User {

    private String name;
    private String email;
    private String password;

    // Default constructor
    public User() {
    }

    // Constructor with all fields
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Constructor without password (for display purposes)
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Utility methods

    /**
     * Get first name from full name
     */
    public String getFirstName() {
        if (name != null && !name.trim().isEmpty()) {
            String[] nameParts = name.trim().split(" ");
            return nameParts[0];
        }
        return "";
    }

    /**
     * Get last name from full name
     */
    public String getLastName() {
        if (name != null && !name.trim().isEmpty()) {
            String[] nameParts = name.trim().split(" ");
            if (nameParts.length > 1) {
                return nameParts[nameParts.length - 1];
            }
        }
        return "";
    }

    /**
     * Get initials from name
     */
    public String getInitials() {
        if (name != null && !name.trim().isEmpty()) {
            String[] nameParts = name.trim().split(" ");
            StringBuilder initials = new StringBuilder();

            for (String part : nameParts) {
                if (!part.isEmpty()) {
                    initials.append(part.charAt(0));
                }
            }

            return initials.toString().toUpperCase();
        }
        return "?";
    }

    /**
     * Check if user data is valid
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                password != null && !password.isEmpty();
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        User user = (User) obj;
        return email != null ? email.equals(user.email) : user.email == null;
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }
}
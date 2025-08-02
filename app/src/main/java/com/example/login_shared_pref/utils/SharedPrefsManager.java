package com.example.login_shared_pref.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages user session data using SharedPreferences
 * Handles login state, user credentials, and preferences
 */
public class SharedPrefsManager {

    private static final String PREF_NAME = "LoginAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PASSWORD = "userPassword"; // Note: In real apps, never store passwords in plain text
    private static final String KEY_REMEMBER_ME = "rememberMe";
    private static final String KEY_FIRST_TIME = "firstTime";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    // Singleton instance
    private static SharedPrefsManager instance;

    private SharedPrefsManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Get singleton instance of SharedPrefsManager
     */
    public static synchronized SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Save user login session
     */
    public void createLoginSession(String email, String name, String password, boolean rememberMe) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);

        // Only store password if "Remember Me" is checked
        if (rememberMe) {
            editor.putString(KEY_USER_PASSWORD, password);
            editor.putBoolean(KEY_REMEMBER_ME, true);
        } else {
            editor.remove(KEY_USER_PASSWORD);
            editor.putBoolean(KEY_REMEMBER_ME, false);
        }

        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get current user's email
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    /**
     * Get current user's name
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    /**
     * Get saved password (only if remember me was enabled)
     */
    public String getSavedPassword() {
        return sharedPreferences.getString(KEY_USER_PASSWORD, "");
    }

    /**
     * Check if remember me was enabled
     */
    public boolean isRememberMeEnabled() {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
    }

    /**
     * Register a new user
     */
    public boolean registerUser(String email, String name, String password) {
        // Check if user already exists
        if (isUserExists(email)) {
            return false;
        }

        // Save user data
        editor.putString("user_" + email + "_name", name);
        editor.putString("user_" + email + "_password", password);
        editor.apply();

        return true;
    }

    /**
     * Check if user exists
     */
    public boolean isUserExists(String email) {
        return sharedPreferences.contains("user_" + email + "_password");
    }

    /**
     * Validate user credentials
     */
    public boolean validateUser(String email, String password) {
        String savedPassword = sharedPreferences.getString("user_" + email + "_password", "");
        return !savedPassword.isEmpty() && savedPassword.equals(password);
    }

    /**
     * Get user's name by email
     */
    public String getUserNameByEmail(String email) {
        return sharedPreferences.getString("user_" + email + "_name", "");
    }

    /**
     * Logout user and clear session
     */
    public void logout() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);

        // If Remember Me is enabled, keep email for auto-fill but remove password for security
        if (isRememberMeEnabled()) {
            // Keep email and name for convenience
            // Keep remember me flag
            // Remove password for security (user will need to re-enter)
            editor.remove(KEY_USER_PASSWORD);
        } else {
            // If Remember Me is disabled, clear everything
            editor.remove(KEY_USER_EMAIL);
            editor.remove(KEY_USER_NAME);
            editor.remove(KEY_USER_PASSWORD);
            editor.putBoolean(KEY_REMEMBER_ME, false);
        }

        editor.apply();
    }

    /**
     * Clear all user data (complete logout)
     */
    public void clearAllData() {
        editor.clear();
        editor.apply();
    }

    public boolean shouldMaintainSession() {
        // If user is logged in but Remember Me is disabled, clear session
        if (isLoggedIn() && !isRememberMeEnabled()) {
            logout();
            return false;
        }

        return isLoggedIn();
    }
    /**
     * Check if this is the first time opening the app
     */
    public boolean isFirstTime() {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME, true);
    }

    /**
     * Set first time flag to false
     */
    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(KEY_FIRST_TIME, isFirstTime);
        editor.apply();
    }

    /**
     * Update user profile
     */
    public void updateUserProfile(String name) {
        String currentEmail = getUserEmail();
        editor.putString(KEY_USER_NAME, name);
        editor.putString("user_" + currentEmail + "_name", name);
        editor.apply();
    }

    /**
     * Change password
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        String email = getUserEmail();
        if (validateUser(email, currentPassword)) {
            editor.putString("user_" + email + "_password", newPassword);
            if (isRememberMeEnabled()) {
                editor.putString(KEY_USER_PASSWORD, newPassword);
            }
            editor.apply();
            return true;
        }
        return false;
    }
}
package com.example.login_shared_pref.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login_shared_pref.R;
import com.example.login_shared_pref.utils.SharedPrefsManager;
import com.example.login_shared_pref.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Register Activity - User registration screen
 * Handles new user registration with validation
 */
public class RegisterActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack;
    private TextInputLayout tilFullName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLoginLink;

    // Utils
    private SharedPrefsManager sharedPrefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        initializeUtils();
        setupClickListeners();

        // Add entrance animation
        findViewById(R.id.card_register_form).startAnimation(
                android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_in_up)
        );
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        tilFullName = findViewById(R.id.til_full_name);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);
    }

    /**
     * Initialize utility classes
     */
    private void initializeUtils() {
        sharedPrefsManager = SharedPrefsManager.getInstance(this);
    }

    /**
     * Setup click listeners for all interactive elements
     */
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        btnRegister.setOnClickListener(v -> attemptRegistration());
        tvLoginLink.setOnClickListener(v -> navigateToLogin());

        // Real-time validation
        etFullName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateName();
        });

        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateEmail();
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validatePassword();
        });

        etConfirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateConfirmPassword();
        });
    }

    /**
     * Attempt to register new user
     */
    private void attemptRegistration() {
        // Clear previous errors
        clearErrors();

        // Get input values
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validate inputs
        ValidationUtils.ValidationResult validation = ValidationUtils.validateRegistration(
                fullName, email, password, confirmPassword);

        if (!validation.isValid()) {
            showValidationErrors(validation);
            return;
        }

        // Check if user already exists
        if (sharedPrefsManager.isUserExists(email)) {
            tilEmail.setError(getString(R.string.error_user_exists));
            showSnackbar(getString(R.string.error_user_exists), true);
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Simulate network delay for better UX
        btnRegister.postDelayed(() -> {
            if (registerUser(fullName, email, password)) {
                handleRegistrationSuccess(fullName, email, password);
            } else {
                handleRegistrationFailure();
            }
            setLoadingState(false);
        }, 1500);
    }

    /**
     * Register new user
     */
    private boolean registerUser(String fullName, String email, String password) {
        return sharedPrefsManager.registerUser(email, fullName, password);
    }

    /**
     * Handle successful registration
     */
    private void handleRegistrationSuccess(String fullName, String email, String password) {
        // Show success message
        showSnackbar(getString(R.string.success_registration), false);

        // Auto-login the user after successful registration
        sharedPrefsManager.createLoginSession(email, fullName, password, false);

        // Navigate to main activity
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        // Add transition animation
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Handle registration failure
     */
    private void handleRegistrationFailure() {
        showSnackbar(getString(R.string.error_registration_failed), true);

        // Shake animation for register button
        btnRegister.startAnimation(
                android.view.animation.AnimationUtils.loadAnimation(this, R.anim.shake)
        );
    }

    /**
     * Validate name field
     */
    private boolean validateName() {
        String name = etFullName.getText().toString().trim();
        String error = ValidationUtils.getNameError(name);
        tilFullName.setError(error);
        return error == null;
    }

    /**
     * Validate email field
     */
    private boolean validateEmail() {
        String email = etEmail.getText().toString().trim();
        String error = ValidationUtils.getEmailError(email);

        // Additional check for existing user
        if (error == null && sharedPrefsManager.isUserExists(email)) {
            error = getString(R.string.error_user_exists);
        }

        tilEmail.setError(error);
        return error == null;
    }

    /**
     * Validate password field
     */
    private boolean validatePassword() {
        String password = etPassword.getText().toString();
        String error = ValidationUtils.getPasswordError(password);
        tilPassword.setError(error);

        // Also validate confirm password if it has text
        if (error == null && !etConfirmPassword.getText().toString().isEmpty()) {
            validateConfirmPassword();
        }

        return error == null;
    }

    /**
     * Validate confirm password field
     */
    private boolean validateConfirmPassword() {
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String error = ValidationUtils.getConfirmPasswordError(password, confirmPassword);
        tilConfirmPassword.setError(error);
        return error == null;
    }

    /**
     * Show validation errors
     */
    private void showValidationErrors(ValidationUtils.ValidationResult validation) {
        if (validation.getNameError() != null) {
            tilFullName.setError(validation.getNameError());
        }
        if (validation.getEmailError() != null) {
            tilEmail.setError(validation.getEmailError());
        }
        if (validation.getPasswordError() != null) {
            tilPassword.setError(validation.getPasswordError());
        }
        if (validation.getConfirmPasswordError() != null) {
            tilConfirmPassword.setError(validation.getConfirmPasswordError());
        }
    }

    /**
     * Clear all error messages
     */
    private void clearErrors() {
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    /**
     * Set loading state for register button
     */
    private void setLoadingState(boolean isLoading) {
        btnRegister.setEnabled(!isLoading);
        btnRegister.setText(isLoading ? getString(R.string.loading) : getString(R.string.btn_register_submit));

        if (isLoading) {
            btnRegister.setIcon(null);
        } else {
            btnRegister.setIconResource(R.drawable.ic_register);
        }

        // Disable input fields during registration
        etFullName.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
        etConfirmPassword.setEnabled(!isLoading);
    }

    /**
     * Navigate back to login activity
     */
    private void navigateToLogin() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Show snackbar message
     */
    private void showSnackbar(String message, boolean isError) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message,
                isError ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT);

        if (isError) {
            snackbar.setBackgroundTint(getResources().getColor(R.color.error_red, getTheme()));
        } else {
            snackbar.setBackgroundTint(getResources().getColor(R.color.success_green, getTheme()));
        }

        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
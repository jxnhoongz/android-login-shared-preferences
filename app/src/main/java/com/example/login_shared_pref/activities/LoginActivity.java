package com.example.login_shared_pref.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login_shared_pref.R;
import com.example.login_shared_pref.utils.SharedPrefsManager;
import com.example.login_shared_pref.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Login Activity - User authentication screen
 * Handles user login with validation and session management
 */
public class LoginActivity extends AppCompatActivity {

    // UI Components
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private MaterialCheckBox cbRememberMe;
    private TextView tvForgotPassword, tvRegisterLink;

    // Utils
    private SharedPrefsManager sharedPrefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeUtils();
        setupClickListeners();
        loadSavedCredentials();

        // Add entrance animation
        findViewById(R.id.card_login_form).startAnimation(
                android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_in_up)
        );
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        cbRememberMe = findViewById(R.id.cb_remember_me);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvRegisterLink = findViewById(R.id.tv_register_link);
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
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegisterLink.setOnClickListener(v -> navigateToRegister());
        tvForgotPassword.setOnClickListener(v -> handleForgotPassword());

        // Real-time validation
        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validateEmail();
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validatePassword();
        });
    }

    /**
     * Load saved credentials if "Remember Me" was previously enabled
     */
    private void loadSavedCredentials() {
        if (sharedPrefsManager.isRememberMeEnabled()) {
            String savedEmail = sharedPrefsManager.getUserEmail();

            // Always fill email if Remember Me was enabled
            if (!savedEmail.isEmpty()) {
                etEmail.setText(savedEmail);
                cbRememberMe.setChecked(true);

                // Focus on password field since email is pre-filled
                etPassword.requestFocus();
            }

            // Only fill password if user is currently logged in (for auto-login scenario)
            // Don't fill password after manual logout for security
            if (sharedPrefsManager.isLoggedIn()) {
                String savedPassword = sharedPrefsManager.getSavedPassword();
                if (!savedPassword.isEmpty()) {
                    etPassword.setText(savedPassword);
                }
            }
        }
    }

    /**
     * Attempt to log in the user
     */
    private void attemptLogin() {
        // Clear previous errors
        clearErrors();

        // Get input values
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        boolean rememberMe = cbRememberMe.isChecked();

        // Validate inputs
        ValidationUtils.ValidationResult validation = ValidationUtils.validateLogin(email, password);

        if (!validation.isValid()) {
            showValidationErrors(validation);
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Simulate network delay for better UX
        btnLogin.postDelayed(() -> {
            if (authenticateUser(email, password, rememberMe)) {
                handleLoginSuccess(email, password, rememberMe);
            } else {
                handleLoginFailure();
            }
            setLoadingState(false);
        }, 1000);
    }

    /**
     * Authenticate user credentials
     */
    private boolean authenticateUser(String email, String password, boolean rememberMe) {
        return sharedPrefsManager.validateUser(email, password);
    }

    /**
     * Handle successful login
     */
    private void handleLoginSuccess(String email, String password, boolean rememberMe) {
        // Get user's name
        String userName = sharedPrefsManager.getUserNameByEmail(email);

        // Create login session
        sharedPrefsManager.createLoginSession(email, userName, password, rememberMe);

        // Show success message
        showSnackbar(getString(R.string.success_login), false);

        // Navigate to main activity
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        // Add transition animation
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Handle login failure
     */
    private void handleLoginFailure() {
        tilEmail.setError(getString(R.string.error_invalid_credentials));
        tilPassword.setError(" "); // Space to show error state
        showSnackbar(getString(R.string.error_invalid_credentials), true);

        // Shake animation for login button
        btnLogin.startAnimation(
                android.view.animation.AnimationUtils.loadAnimation(this, R.anim.shake)
        );
    }

    /**
     * Validate email field
     */
    private boolean validateEmail() {
        String email = etEmail.getText().toString().trim();
        String error = ValidationUtils.getEmailError(email);
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
        return error == null;
    }

    /**
     * Show validation errors
     */
    private void showValidationErrors(ValidationUtils.ValidationResult validation) {
        if (validation.getEmailError() != null) {
            tilEmail.setError(validation.getEmailError());
        }
        if (validation.getPasswordError() != null) {
            tilPassword.setError(validation.getPasswordError());
        }
    }

    /**
     * Clear all error messages
     */
    private void clearErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
    }

    /**
     * Set loading state for login button
     */
    private void setLoadingState(boolean isLoading) {
        btnLogin.setEnabled(!isLoading);
        btnLogin.setText(isLoading ? getString(R.string.loading) : getString(R.string.btn_login));

        if (isLoading) {
            btnLogin.setIcon(null);
        } else {
            btnLogin.setIconResource(R.drawable.ic_login);
        }
    }

    /**
     * Navigate to register activity
     */
    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Handle forgot password click
     */
    private void handleForgotPassword() {
        showSnackbar("Forgot password feature coming soon!", false);
        // TODO: Implement forgot password functionality
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
        // Exit app when back is pressed on login screen
        finishAffinity();
    }
}
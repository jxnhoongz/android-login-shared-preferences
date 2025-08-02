package com.example.login_shared_pref.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.login_shared_pref.R;
import com.example.login_shared_pref.models.User;
import com.example.login_shared_pref.utils.SharedPrefsManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Main Activity - Dashboard after successful login
 * Shows user information and app navigation
 */
public class MainActivity extends AppCompatActivity {

    // UI Components
    private MaterialToolbar toolbar;
    private TextView tvUserInitials, tvWelcomeMessage, tvLastLogin;
    private ImageButton btnLogout;
    private CardView cardProfile, cardSettings;

    // Utils
    private SharedPrefsManager sharedPrefsManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeUtils();
        loadUserData();
        setupClickListeners();
        displayUserInfo();

        // Check if user is actually logged in
        if (!sharedPrefsManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        // Add entrance animation
        findViewById(R.id.card_welcome).startAnimation(
                android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_in_up)
        );
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvUserInitials = findViewById(R.id.tv_user_initials);
        tvWelcomeMessage = findViewById(R.id.tv_welcome_message);
        tvLastLogin = findViewById(R.id.tv_last_login);
        btnLogout = findViewById(R.id.btn_logout);
        cardProfile = findViewById(R.id.card_profile);
        cardSettings = findViewById(R.id.card_settings);
    }

    /**
     * Initialize utility classes
     */
    private void initializeUtils() {
        sharedPrefsManager = SharedPrefsManager.getInstance(this);
    }

    /**
     * Load current user data
     */
    private void loadUserData() {
        String email = sharedPrefsManager.getUserEmail();
        String name = sharedPrefsManager.getUserName();

        currentUser = new User(name, email);
    }

    /**
     * Setup click listeners for all interactive elements
     */
    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
        cardProfile.setOnClickListener(v -> handleProfileClick());
        cardSettings.setOnClickListener(v -> handleSettingsClick());

        // Toolbar menu (if needed in future)
        toolbar.setOnMenuItemClickListener(item -> {
            // Handle menu item clicks
            return true;
        });
    }

    /**
     * Display user information in the UI
     */
    private void displayUserInfo() {
        if (currentUser != null) {
            // Set user initials
            tvUserInitials.setText(currentUser.getInitials());

            // Set welcome message with first name
            String welcomeMessage = getString(R.string.welcome_message, currentUser.getFirstName());
            tvWelcomeMessage.setText(welcomeMessage);

            // Set last login time
            String currentTime = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(new Date());
            tvLastLogin.setText(currentTime);
        }
    }

    /**
     * Show logout confirmation dialog
     */
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage(getString(R.string.logout_confirmation))
                .setPositiveButton(getString(R.string.btn_yes), (dialog, which) -> performLogout())
                .setNegativeButton(getString(R.string.btn_no), null)
                .setIcon(R.drawable.ic_logout)
                .show();
    }

    /**
     * Perform user logout
     */
    private void performLogout() {
        // Clear user session
        sharedPrefsManager.logout();

        // Show logout message
        showSnackbar(getString(R.string.success_logout), false);

        // Redirect to login
        redirectToLogin();
    }

    /**
     * Redirect to login activity
     */
    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        // Add transition animation
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Handle profile card click
     */
    private void handleProfileClick() {
        showSnackbar("Profile feature coming soon!", false);
        // TODO: Navigate to profile activity
        // Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        // startActivity(intent);
    }

    /**
     * Handle settings card click
     */
    private void handleSettingsClick() {
        showSnackbar("Settings feature coming soon!", false);
        // TODO: Navigate to settings activity
        // Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        // startActivity(intent);
    }

    /**
     * Show snackbar message
     */
    private void showSnackbar(String message, boolean isError) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message,
                Snackbar.LENGTH_SHORT);

        if (isError) {
            snackbar.setBackgroundTint(getResources().getColor(R.color.error_red, getTheme()));
        } else {
            snackbar.setBackgroundTint(getResources().getColor(R.color.success_green, getTheme()));
        }

        snackbar.show();
    }

    /**
     * Update user profile information
     */
    public void updateUserProfile(String newName) {
        if (currentUser != null) {
            currentUser.setName(newName);
            sharedPrefsManager.updateUserProfile(newName);
            displayUserInfo();
            showSnackbar("Profile updated successfully!", false);
        }
    }

    /**
     * Get current user information
     */
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void onBackPressed() {
        // Show exit confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Exit", (dialog, which) -> finishAffinity())
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh user data in case it was updated elsewhere
        loadUserData();
        displayUserInfo();

        // Check if user is still logged in
        if (!sharedPrefsManager.isLoggedIn()) {
            redirectToLogin();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save any pending data or state
    }
}
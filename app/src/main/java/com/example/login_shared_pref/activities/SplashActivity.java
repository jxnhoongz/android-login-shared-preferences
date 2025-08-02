package com.example.login_shared_pref.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login_shared_pref.R;
import com.example.login_shared_pref.utils.SharedPrefsManager;

/**
 * Splash screen activity - App entry point
 * Shows app logo and checks login status
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 2.5 seconds

    private SharedPrefsManager sharedPrefsManager;
    private ImageView logoImageView;
    private TextView appNameTextView;
    private TextView taglineTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeViews();
        initializePrefs();
        startAnimations();
        navigateAfterDelay();
    }

    /**
     * Initialize UI components
     */
    private void initializeViews() {
        logoImageView = findViewById(R.id.iv_logo);
        appNameTextView = findViewById(R.id.tv_app_name);
        taglineTextView = findViewById(R.id.tv_tagline);
    }

    /**
     * Initialize SharedPreferences manager
     */
    private void initializePrefs() {
        sharedPrefsManager = SharedPrefsManager.getInstance(this);
    }

    /**
     * Start entrance animations
     */
    private void startAnimations() {
        // Logo animation - scale up with fade in
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_logo_animation);
        logoImageView.startAnimation(logoAnimation);

        // App name animation - slide up with fade in
        Animation nameAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_text_animation);
        appNameTextView.startAnimation(nameAnimation);

        // Tagline animation - fade in with delay
        Animation taglineAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_tagline_animation);
        taglineTextView.startAnimation(taglineAnimation);
    }

    /**
     * Navigate to appropriate screen after splash delay
     */
    private void navigateAfterDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextScreen();
            }
        }, SPLASH_DURATION);
    }

    /**
     * Determine which screen to navigate to based on login status
     */
    private void navigateToNextScreen() {
        Intent intent;

        if (sharedPrefsManager.shouldMaintainSession()) {
            // Session is valid and should be maintained
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // No valid session or Remember Me disabled
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        // Disable back button on splash screen
        // User should wait for automatic navigation
    }
}
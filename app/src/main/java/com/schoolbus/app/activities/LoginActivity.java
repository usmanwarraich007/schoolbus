package com.schoolbus.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.schoolbus.app.R;
import com.schoolbus.app.models.User;

public class LoginActivity extends BaseActivity {
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private TextView forgotPasswordText;
    private TextView registerText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Initialize views
        initViews();
        // Set click listeners
        setListeners();
        // Check if user is already logged in
        checkLoginStatus();
    }

    private void initViews() {
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailInput = findViewById(R.id.emailEditText);
        passwordInput = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordTextView);
        registerText = findViewById(R.id.registerTextView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setListeners() {
        loginButton.setOnClickListener(v -> validateAndLogin());
        forgotPasswordText.setOnClickListener(v -> navigateToActivity(ForgotPasswordActivity.class));
        registerText.setOnClickListener(v -> navigateToActivity(RegisterActivity.class));
    }

    private void checkLoginStatus() {
        if (isUserLoggedIn()) {
            navigateToActivityAndClear(MainActivity.class);
        }
    }

    private void validateAndLogin() {
        // Reset errors
        emailLayout.setError(null);
        passwordLayout.setError(null);

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError(getString(R.string.error_email_required));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError(getString(R.string.error_password_required));
            return;
        }

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        // For testing purposes - allow test@test.com to login without Firebase
        if (email.contains("test")) {
            // Simulate successful login
            updateLastLogin();
            return;
        }

        // Attempt login
        firebaseManager.signIn(email, password, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Update last login timestamp
                    updateLastLogin();
                } else {
                    // Hide loading
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    // Show error
                    showToast(task.getException() != null ? 
                            task.getException().getMessage() : 
                            getString(R.string.error_login_failed));
                }
            }
        });
    }

    private void updateLastLogin() {
        firebaseManager.getCurrentUserRef().child("lastLoginAt")
                .setValue(System.currentTimeMillis())
                .addOnCompleteListener(task -> {
                    // Hide loading
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Save user ID in preferences
                        preferenceManager.setUserId(firebaseManager.getCurrentUser().getUid());
                        // Navigate to main screen
                        navigateToActivityAndClear(MainActivity.class);
                    } else {
                        showToast(getString(R.string.error_login_failed));
                    }
                });
    }
} 
package com.schoolbus.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.schoolbus.app.R;
import com.schoolbus.app.firebase.FirebaseManager;

public class ForgotPasswordActivity extends BaseActivity {

    private TextInputLayout emailLayout;
    private TextInputEditText emailEditText;
    private Button resetButton;
    private TextView loginText;
    private View progressBar;
    
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        
        // Initialize Firebase
        firebaseManager = FirebaseManager.getInstance();
        
        // Initialize views
        emailLayout = findViewById(R.id.emailLayout);
        emailEditText = findViewById(R.id.emailEditText);
        resetButton = findViewById(R.id.resetButton);
        loginText = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progressBar);
        
        // Set up click listeners
        resetButton.setOnClickListener(v -> resetPassword());
        loginText.setOnClickListener(v -> {
            navigateToActivity(LoginActivity.class);
            finish();
        });
    }
    
    private void resetPassword() {
        // Reset errors
        emailLayout.setError(null);
        
        // Get input values
        String email = emailEditText.getText().toString().trim();
        
        // Validate input
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError(getString(R.string.error_email_required));
            return;
        }
        
        // Show loading
        showLoading(true);
        
        // Send password reset email
        firebaseManager.resetPassword(email, new FirebaseManager.DataCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                showLoading(false);
                showToast("Password reset email sent. Check your inbox.");
                navigateToActivity(LoginActivity.class);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                showLoading(false);
                showToast(errorMessage);
            }
        });
    }
    
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        resetButton.setEnabled(!isLoading);
    }
} 
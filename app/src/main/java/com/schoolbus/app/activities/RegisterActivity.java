package com.schoolbus.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.schoolbus.app.R;
import com.schoolbus.app.firebase.FirebaseManager;
import com.schoolbus.app.models.User;
import com.schoolbus.app.utils.Constants;

public class RegisterActivity extends BaseActivity {

    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout phoneLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    
    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText phoneEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    
    private Button registerButton;
    private TextView loginTextView;
    private View progressBar;
    private RadioGroup userTypeRadioGroup;
    
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // Initialize Firebase
        firebaseManager = FirebaseManager.getInstance();
        
        // Initialize views
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);
        progressBar = findViewById(R.id.progressBar);
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        
        // Set up click listeners
        registerButton.setOnClickListener(v -> registerUser());
        loginTextView.setOnClickListener(v -> navigateToActivity(LoginActivity.class));
    }
    
    private void registerUser() {
        // Reset errors
        nameLayout.setError(null);
        emailLayout.setError(null);
        phoneLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
        
        // Get input values
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        
        // Validate input
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError(getString(R.string.error_name_required));
            return;
        }
        
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError(getString(R.string.error_email_required));
            return;
        }
        
        if (TextUtils.isEmpty(phone)) {
            phoneLayout.setError(getString(R.string.error_phone_required));
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError(getString(R.string.error_password_required));
            return;
        }
        
        if (password.length() < 6) {
            passwordLayout.setError(getString(R.string.error_password_too_short));
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError(getString(R.string.error_passwords_dont_match));
            return;
        }
        
        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        
        // Get user type
        int selectedId = userTypeRadioGroup.getCheckedRadioButtonId();
        String userType = selectedId == R.id.parentRadioButton ? Constants.USER_TYPE_PARENT : Constants.USER_TYPE_DRIVER;
        
        // Log registration attempt
        Log.d("RegisterActivity", "Attempting to register user: " + email + " as " + userType);
        
        // Register user
        firebaseManager.registerUser(email, password, new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                Log.d("RegisterActivity", "User registered successfully with ID: " + userId);
                
                // Create user profile
                User user = new User();
                user.setName(name);
                user.setEmail(email);
                user.setPhone(phone);
                user.setType(userType);
                
                firebaseManager.createUserProfile(userId, user, new FirebaseManager.DataCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        Log.d("RegisterActivity", "User profile created successfully");
                        progressBar.setVisibility(View.GONE);
                        showToast(getString(R.string.registration_successful));
                        navigateToActivity(LoginActivity.class);
                        finish();
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        Log.e("RegisterActivity", "Failed to create user profile: " + errorMessage);
                        progressBar.setVisibility(View.GONE);
                        showToast(getString(R.string.error_creating_profile));
                    }
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e("RegisterActivity", "Registration failed: " + errorMessage);
                progressBar.setVisibility(View.GONE);
                showToast(errorMessage);
            }
        });
    }
    
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        registerButton.setEnabled(!isLoading);
    }
} 
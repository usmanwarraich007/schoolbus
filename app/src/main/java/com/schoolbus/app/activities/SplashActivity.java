package com.schoolbus.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.schoolbus.app.R;
import com.schoolbus.app.firebase.FirebaseManager;
import com.schoolbus.app.utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DELAY = 2000; // 2 seconds
    
    private FirebaseManager firebaseManager;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Initialize managers
        firebaseManager = FirebaseManager.getInstance();
        preferenceManager = PreferenceManager.getInstance(this);
        
        // Delay and navigate
        new Handler(Looper.getMainLooper()).postDelayed(this::checkLoginStatus, SPLASH_DELAY);
    }
    
    private void checkLoginStatus() {
        if (firebaseManager.isUserLoggedIn() && preferenceManager.getUserId() != null) {
            // User is logged in, navigate to main screen
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // User is not logged in, navigate to login screen
            startActivity(new Intent(this, LoginActivity.class));
        }
        
        // Finish splash activity
        finish();
    }
} 
package com.schoolbus.app;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SchoolBusApplication extends Application {
    private static final String TAG = "SchoolBusApp";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this);
            Log.d(TAG, "Firebase initialized successfully");
            
            // Enable Firebase offline capabilities - IMPORTANT: This must be called before any Firebase database reference is created
            try {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                Log.d(TAG, "Firebase persistence enabled");
            } catch (Exception e) {
                Log.e(TAG, "Error enabling Firebase persistence: " + e.getMessage());
            }
            
            // Check Firebase connection
            checkFirebaseConnection();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase: " + e.getMessage(), e);
        }
    }
    
    private void checkFirebaseConnection() {
        try {
            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class) != null && snapshot.getValue(Boolean.class);
                    if (connected) {
                        Log.d(TAG, "Firebase connection established successfully");
                    } else {
                        Log.w(TAG, "Firebase connection lost or not established");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e(TAG, "Firebase connection listener was cancelled: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error checking Firebase connection: " + e.getMessage(), e);
        }
    }
} 
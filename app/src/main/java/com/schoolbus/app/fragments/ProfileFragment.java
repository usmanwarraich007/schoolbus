package com.schoolbus.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.schoolbus.app.R;
import com.schoolbus.app.activities.LoginActivity;
import com.schoolbus.app.firebase.FirebaseManager;
import com.schoolbus.app.models.User;
import com.schoolbus.app.utils.DummyDataGenerator;
import com.schoolbus.app.utils.PreferenceManager;

public class ProfileFragment extends Fragment {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private Button logoutButton;
    private Button editProfileButton;
    private Button generateDummyDataButton;
    private View progressBar;
    
    private FirebaseManager firebaseManager;
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // Initialize views
        initViews(view);
        
        // Initialize Firebase and Preferences
        firebaseManager = FirebaseManager.getInstance();
        preferenceManager = PreferenceManager.getInstance(requireContext());
        
        // Set up button listeners
        setupClickListeners();
        
        // Load user profile
        loadUserProfile();
        
        return view;
    }
    
    private void initViews(View view) {
        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        generateDummyDataButton = view.findViewById(R.id.generateDummyDataButton);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        editProfileButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Edit profile feature coming soon", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            logout();
        });
        
        generateDummyDataButton.setOnClickListener(v -> {
            generateDummyData();
        });
    }
    
    private void loadUserProfile() {
        String userId = preferenceManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            return;
        }
        
        firebaseManager.getUserProfile(userId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        updateUI(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    
    private void updateUI(User user) {
        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
        phoneTextView.setText(user.getPhone());
    }
    
    private void logout() {
        firebaseManager.signOut();
        preferenceManager.clearAll();
        
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    
    private void generateDummyData() {
        String userId = preferenceManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        generateDummyDataButton.setEnabled(false);
        
        // Create dummy data generator
        DummyDataGenerator dummyDataGenerator = new DummyDataGenerator(userId);
        
        // Generate and add dummy data
        dummyDataGenerator.generateAllData(new DummyDataGenerator.DataGenerationCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        generateDummyDataButton.setEnabled(true);
                        Toast.makeText(requireContext(), "Dummy data generated successfully", Toast.LENGTH_SHORT).show();
                    });
                }
            }
            
            @Override
            public void onError(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        generateDummyDataButton.setEnabled(true);
                        Toast.makeText(requireContext(), "Error generating dummy data: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
} 
package com.schoolbus.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.schoolbus.app.R;
import com.schoolbus.app.adapters.NotificationAdapter;
import com.schoolbus.app.firebase.FirebaseManager;
import com.schoolbus.app.models.Notification;
import com.schoolbus.app.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;
    private NotificationAdapter adapter;
    private List<Notification> notifications;
    private FirebaseManager firebaseManager;
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        
        // Initialize views
        initViews(view);
        
        // Initialize Firebase and Preferences
        firebaseManager = FirebaseManager.getInstance();
        preferenceManager = new PreferenceManager(requireContext());
        
        // Initialize notifications list
        setupRecyclerView();
        
        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadNotifications);
        
        // Load notifications
        loadNotifications();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.notificationsRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        emptyView = view.findViewById(R.id.emptyView);
    }

    private void setupRecyclerView() {
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(requireContext(), notifications, notification -> {
            // Handle notification click
            // Show notification details
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        swipeRefreshLayout.setRefreshing(true);
        
        String userId = preferenceManager.getUserId();
        if (userId != null && !userId.isEmpty()) {
            firebaseManager.getNotifications(userId, new FirebaseManager.DataCallback<List<Notification>>() {
                @Override
                public void onSuccess(List<Notification> data) {
                    notifications.clear();
                    if (data != null && !data.isEmpty()) {
                        notifications.addAll(data);
                        emptyView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(String errorMessage) {
                    emptyView.setText(R.string.msg_no_data);
                    emptyView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            emptyView.setText(R.string.msg_no_data);
            emptyView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
} 
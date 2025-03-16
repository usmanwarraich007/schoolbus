package com.schoolbus.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.schoolbus.app.R;
import com.schoolbus.app.adapters.BusAdapter;
import com.schoolbus.app.adapters.NotificationAdapter;
import com.schoolbus.app.firebase.FirebaseManager;
import com.schoolbus.app.models.Bus;
import com.schoolbus.app.models.Notification;
import com.schoolbus.app.utils.Constants;
import com.schoolbus.app.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView routesRecyclerView;
    private RecyclerView notificationsRecyclerView;
    private TextView emptyRoutesTextView;
    private TextView emptyNotificationsTextView;
    private MaterialButton trackBusButton;
    
    private FirebaseManager firebaseManager;
    private PreferenceManager preferenceManager;
    
    private BusAdapter busAdapter;
    private NotificationAdapter notificationAdapter;
    
    private List<Bus> busList;
    private List<Notification> notificationList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize Firebase and Preference managers
        firebaseManager = FirebaseManager.getInstance();
        preferenceManager = PreferenceManager.getInstance(requireContext());
        
        // Initialize views
        initViews(view);
        
        // Set up recycler views
        setupRecyclerViews();
        
        // Set up swipe refresh
        setupSwipeRefresh();
        
        // Set up button click listeners
        setupClickListeners();
        
        // Load data
        loadData();
        
        return view;
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        routesRecyclerView = view.findViewById(R.id.routesRecyclerView);
        notificationsRecyclerView = view.findViewById(R.id.notificationsRecyclerView);
        emptyRoutesTextView = view.findViewById(R.id.emptyRoutesTextView);
        emptyNotificationsTextView = view.findViewById(R.id.emptyNotificationsTextView);
        trackBusButton = view.findViewById(R.id.trackBusButton);
    }

    private void setupRecyclerViews() {
        // Set up active buses recycler view
        busList = new ArrayList<>();
        busAdapter = new BusAdapter(requireContext(), busList, bus -> {
            // Handle bus item click - navigate to tracking screen
            if (getActivity() != null) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA_BUS_ID, bus.getId());
                
                Fragment trackingFragment = new TrackingFragment();
                trackingFragment.setArguments(bundle);
                
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, trackingFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        
        routesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        routesRecyclerView.setAdapter(busAdapter);
        
        // Set up recent alerts recycler view
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(requireContext(), notificationList, notification -> {
            // Handle notification item click - mark as read and show details
            if (!notification.isRead()) {
                notification.setRead(true);
                firebaseManager.updateNotificationReadStatus(notification.getId(), true, task -> {
                    if (!task.isSuccessful() && getContext() != null) {
                        Toast.makeText(getContext(), "Failed to mark notification as read", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            // Show notification details
            // TODO: Implement notification details dialog or screen
        });
        
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationsRecyclerView.setAdapter(notificationAdapter);
    }

    private void setupSwipeRefresh() {
        // Check if swipeRefreshLayout is null to prevent crash
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.accent);
            swipeRefreshLayout.setOnRefreshListener(this::loadData);
        }
    }

    private void setupClickListeners() {
        trackBusButton.setOnClickListener(v -> {
            // Navigate to routes fragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, new RoutesFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void loadData() {
        // Show refresh indicator if available
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        
        loadActiveBuses();
        loadRecentAlerts();
    }

    private void loadActiveBuses() {
        firebaseManager.getActiveBuses(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                busList.clear();
                
                if (dataSnapshot.exists()) {
                    busList.addAll(firebaseManager.snapshotToBusList(dataSnapshot));
                    
                    // Sort buses by bus number
                    Collections.sort(busList, (b1, b2) -> 
                            b1.getBusNumber().compareTo(b2.getBusNumber()));
                    
                    busAdapter.notifyDataSetChanged();
                    emptyRoutesTextView.setVisibility(busList.isEmpty() ? View.VISIBLE : View.GONE);
                    routesRecyclerView.setVisibility(busList.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    emptyRoutesTextView.setVisibility(View.VISIBLE);
                    routesRecyclerView.setVisibility(View.GONE);
                }
                
                // Hide refresh indicator if available
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hide refresh indicator if available
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load buses: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadRecentAlerts() {
        String userId = preferenceManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            // Hide refresh indicator if available
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            return;
        }
        
        firebaseManager.getUserNotifications(userId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear();
                
                if (dataSnapshot.exists()) {
                    notificationList.addAll(firebaseManager.snapshotToNotificationList(dataSnapshot));
                    
                    // Sort notifications by timestamp (newest first)
                    Collections.sort(notificationList, (n1, n2) -> 
                            Long.compare(n2.getTimestamp(), n1.getTimestamp()));
                    
                    // Limit to 5 most recent notifications
                    if (notificationList.size() > 5) {
                        notificationList = notificationList.subList(0, 5);
                    }
                    
                    notificationAdapter.notifyDataSetChanged();
                    emptyNotificationsTextView.setVisibility(notificationList.isEmpty() ? View.VISIBLE : View.GONE);
                    notificationsRecyclerView.setVisibility(notificationList.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    emptyNotificationsTextView.setVisibility(View.VISIBLE);
                    notificationsRecyclerView.setVisibility(View.GONE);
                }
                
                // Hide refresh indicator if available
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hide refresh indicator if available
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load notifications: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment resumes
        loadData();
    }
} 
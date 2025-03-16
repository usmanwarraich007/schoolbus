package com.schoolbus.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.schoolbus.app.R;
import com.schoolbus.app.adapters.StopAdapter;
import com.schoolbus.app.firebase.FirebaseManager;
import com.schoolbus.app.models.Bus;
import com.schoolbus.app.models.Route;
import com.schoolbus.app.models.Stop;
import com.schoolbus.app.utils.Constants;
import com.schoolbus.app.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class RouteDetailsFragment extends Fragment {
    private Toolbar toolbar;
    private TextView routeNameTextView;
    private TextView busNumberTextView;
    private TextView driverNameTextView;
    private TextView statusTextView;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private TextView daysTextView;
    private RecyclerView stopsRecyclerView;
    private ProgressBar progressBar;
    private Button trackBusButton;
    
    private FirebaseManager firebaseManager;
    private PreferenceManager preferenceManager;
    
    private String routeId;
    private Route currentRoute;
    private Bus currentBus;
    private List<Stop> stops;
    private StopAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_details, container, false);
        
        // Initialize views
        initViews(view);
        
        // Initialize Firebase
        firebaseManager = FirebaseManager.getInstance();
        preferenceManager = PreferenceManager.getInstance(requireContext());
        
        // Get route ID from arguments
        Bundle args = getArguments();
        if (args != null) {
            routeId = args.getString(Constants.EXTRA_ROUTE_ID);
        }
        
        // Initialize stops list and adapter
        setupRecyclerView();
        
        // Load route details
        if (routeId != null && !routeId.isEmpty()) {
            loadRouteDetails();
        } else {
            showError("Invalid route ID");
        }
        
        return view;
    }
    
    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        routeNameTextView = view.findViewById(R.id.routeNameTextView);
        busNumberTextView = view.findViewById(R.id.busNumberTextView);
        driverNameTextView = view.findViewById(R.id.driverNameTextView);
        statusTextView = view.findViewById(R.id.statusTextView);
        startTimeTextView = view.findViewById(R.id.startTimeTextView);
        endTimeTextView = view.findViewById(R.id.endTimeTextView);
        daysTextView = view.findViewById(R.id.daysTextView);
        stopsRecyclerView = view.findViewById(R.id.stopsRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        trackBusButton = view.findViewById(R.id.trackBusButton);
        
        // Set up toolbar
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    private void setupRecyclerView() {
        stops = new ArrayList<>();
        adapter = new StopAdapter(requireContext(), stops, new StopAdapter.OnStopClickListener() {
            @Override
            public void onStopClick(Stop stop) {
                // Handle stop click
            }

            @Override
            public void onViewOnMapClick(Stop stop) {
                // Handle view on map click
            }
        });
        stopsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        stopsRecyclerView.setAdapter(adapter);
    }
    
    private void loadRouteDetails() {
        showLoading();
        
        firebaseManager.getRouteDetails(routeId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentRoute = dataSnapshot.getValue(Route.class);
                    if (currentRoute != null) {
                        currentRoute.setId(dataSnapshot.getKey());
                        updateRouteUI();
                        loadBusDetails();
                        loadStops();
                    } else {
                        showError("Failed to load route details");
                    }
                } else {
                    showError("Route not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showError("Error: " + databaseError.getMessage());
            }
        });
    }
    
    private void loadBusDetails() {
        if (currentRoute != null && currentRoute.getBusId() != null) {
            firebaseManager.getBusDetails(currentRoute.getBusId(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        currentBus = dataSnapshot.getValue(Bus.class);
                        if (currentBus != null) {
                            currentBus.setId(dataSnapshot.getKey());
                            updateBusUI();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Just log error, don't show to user
                }
            });
        }
    }
    
    private void loadStops() {
        if (currentRoute != null && currentRoute.getStopsMap() != null && !currentRoute.getStopsMap().isEmpty()) {
            for (String stopId : currentRoute.getStopsMap().keySet()) {
                firebaseManager.getStopDetails(stopId, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Stop stop = dataSnapshot.getValue(Stop.class);
                            if (stop != null) {
                                stop.setId(dataSnapshot.getKey());
                                stops.add(stop);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Just log error, don't show to user
                    }
                });
            }
        }
    }
    
    private void updateRouteUI() {
        if (currentRoute != null) {
            routeNameTextView.setText(currentRoute.getName());
            
            // Set schedule info
            if (currentRoute.getStartTime() != null) {
                startTimeTextView.setText("Start Time: " + currentRoute.getStartTime());
            }
            
            if (currentRoute.getEndTime() != null) {
                endTimeTextView.setText("End Time: " + currentRoute.getEndTime());
            }
            
            if (currentRoute.getDays() != null && !currentRoute.getDays().isEmpty()) {
                StringBuilder daysStr = new StringBuilder();
                for (int i = 0; i < currentRoute.getDays().size(); i++) {
                    if (i > 0) {
                        daysStr.append(", ");
                    }
                    daysStr.append(currentRoute.getDays().get(i));
                }
                daysTextView.setText("Days: " + daysStr.toString());
            }
        }
    }
    
    private void updateBusUI() {
        if (currentBus != null) {
            busNumberTextView.setText("Bus Number: " + currentBus.getBusNumber());
            
            if (currentBus.getDriverName() != null) {
                driverNameTextView.setText("Driver: " + currentBus.getDriverName());
            }
            
            if (currentBus.getStatus() != null) {
                statusTextView.setText(currentBus.getStatus());
                
                // Set status color
                int statusColor;
                switch (currentBus.getStatus()) {
                    case Constants.BUS_STATUS_ACTIVE:
                        statusColor = getResources().getColor(R.color.status_active);
                        break;
                    case Constants.BUS_STATUS_INACTIVE:
                        statusColor = getResources().getColor(R.color.status_inactive);
                        break;
                    case Constants.BUS_STATUS_MAINTENANCE:
                        statusColor = getResources().getColor(R.color.status_warning);
                        break;
                    default:
                        statusColor = getResources().getColor(R.color.text_secondary);
                        break;
                }
                statusTextView.setTextColor(statusColor);
            }
        }
    }
    
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }
    
    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }
    
    private void showError(String message) {
        hideLoading();
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
} 
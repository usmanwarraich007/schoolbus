package com.schoolbus.app.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.schoolbus.app.R;
import com.schoolbus.app.firebase.FirebaseManager;
import com.schoolbus.app.models.Bus;
import com.schoolbus.app.models.Route;
import com.schoolbus.app.models.Stop;
import com.schoolbus.app.utils.Constants;
import com.schoolbus.app.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class TrackingFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ProgressBar mapProgressBar;
    private CardView busInfoCard;
    private TextView busNumberText;
    private TextView routeNameText;
    private TextView statusText;
    private TextView driverNameText;
    private TextView nextStopText;
    private TextView estimatedArrivalText;
    private TextView lastUpdatedText;
    private TextView distanceText;
    
    private FirebaseManager firebaseManager;
    private PreferenceManager preferenceManager;
    
    private String busId;
    private Bus currentBus;
    private Route currentRoute;
    private Stop nextStop;
    
    private ValueEventListener busListener;
    private ValueEventListener routeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);
        
        // Initialize Firebase and Preferences
        firebaseManager = FirebaseManager.getInstance();
        preferenceManager = PreferenceManager.getInstance(requireContext());
        
        // Get bus ID from arguments
        if (getArguments() != null) {
            busId = getArguments().getString(Constants.EXTRA_BUS_ID);
        }
        
        // Initialize views
        initViews(view);
        
        // Set up map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        
        // Set up click listeners
        setupClickListeners();
        
        // Load bus data
        if (busId != null && !busId.isEmpty()) {
            loadBusData();
        }
        
        return view;
    }

    private void initViews(View view) {
        mapProgressBar = view.findViewById(R.id.progressBar);
        busInfoCard = view.findViewById(R.id.infoCardView);
        busNumberText = view.findViewById(R.id.busNumberTextView);
        routeNameText = view.findViewById(R.id.routeNameTextView);
        statusText = view.findViewById(R.id.statusTextView);
        driverNameText = null;
        nextStopText = view.findViewById(R.id.nextStopTextView);
        estimatedArrivalText = view.findViewById(R.id.estimatedArrivalTextView);
        lastUpdatedText = view.findViewById(R.id.lastUpdatedTextView);
        distanceText = view.findViewById(R.id.distanceTextView);
        
        // Get map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupClickListeners() {
        // Set up click listeners for buttons
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        // Configure map settings
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        
        // Enable my location if permission is granted
        if (ActivityCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        
        // Load bus data
        if (busId != null && !busId.isEmpty()) {
            loadBusData();
        } else {
            showError("Bus ID not provided");
        }
    }

    private void loadBusData() {
        showLoading(true);
        
        busListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentBus = dataSnapshot.getValue(Bus.class);
                    if (currentBus != null) {
                        currentBus.setId(dataSnapshot.getKey());
                        updateBusInfo();
                        
                        // Load route data
                        if (currentBus.getRouteId() != null) {
                            loadRouteData(currentBus.getRouteId());
                        } else {
                            showLoading(false);
                            updateMap();
                        }
                    } else {
                        showError("Failed to load bus data");
                    }
                } else {
                    showError("Bus not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showLoading(false);
                showError("Failed to load bus data: " + databaseError.getMessage());
            }
        };
        
        firebaseManager.getBusDetails(busId, busListener);
    }

    private void loadRouteData(String routeId) {
        routeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showLoading(false);
                
                if (dataSnapshot.exists()) {
                    currentRoute = dataSnapshot.getValue(Route.class);
                    if (currentRoute != null) {
                        currentRoute.setId(dataSnapshot.getKey());
                        updateRouteInfo();
                        updateMap();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showLoading(false);
                showError("Failed to load route data: " + databaseError.getMessage());
            }
        };
        
        firebaseManager.getRouteDetails(routeId, routeListener);
    }

    private void updateBusInfo() {
        if (currentBus == null) return;
        
        busNumberText.setText(currentBus.getBusNumber());
        routeNameText.setText(currentBus.getRouteName());
        statusText.setText(currentBus.getStatus());
        
        // Only set driver name if the TextView exists
        if (driverNameText != null) {
            driverNameText.setText(currentBus.getDriverName());
        }
        
        // Set status text color based on status
        int statusColor;
        switch (currentBus.getStatus()) {
            case Constants.BUS_STATUS_ACTIVE:
                statusColor = requireContext().getResources().getColor(R.color.status_active);
                break;
            case Constants.BUS_STATUS_INACTIVE:
                statusColor = requireContext().getResources().getColor(R.color.status_inactive);
                break;
            case Constants.BUS_STATUS_MAINTENANCE:
                statusColor = requireContext().getResources().getColor(R.color.status_warning);
                break;
            default:
                statusColor = requireContext().getResources().getColor(R.color.text_secondary);
                break;
        }
        statusText.setTextColor(statusColor);
        
        // Format last updated time
        if (currentBus.getLastUpdated() > 0) {
            CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                    currentBus.getLastUpdated(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS);
            lastUpdatedText.setText(relativeTime);
        } else {
            lastUpdatedText.setText("Unknown");
        }
    }

    private void updateRouteInfo() {
        if (currentRoute == null || currentBus == null) return;
        
        // Find next stop
        Route.Stop nextStop = findNextStop();
        if (nextStop != null) {
            nextStopText.setText(nextStop.getName());
            estimatedArrivalText.setText(nextStop.getArrivalTime());
        } else {
            nextStopText.setText("N/A");
            estimatedArrivalText.setText("N/A");
        }
    }

    private Route.Stop findNextStop() {
        if (currentRoute == null || currentRoute.getStops() == null || 
                currentRoute.getStops().isEmpty() || currentBus == null || 
                currentBus.getCurrentLocation() == null) {
            return null;
        }
        
        // Find the closest stop that hasn't been reached yet
        // This is a simplified approach - in a real app, you would use more sophisticated logic
        Route.Stop closestStop = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Route.Stop stop : currentRoute.getStops()) {
            double distance = calculateDistance(
                    currentBus.getCurrentLocation().getLatitude(),
                    currentBus.getCurrentLocation().getLongitude(),
                    stop.getLatitude(),
                    stop.getLongitude());
            
            if (distance < minDistance) {
                minDistance = distance;
                closestStop = stop;
            }
        }
        
        return closestStop;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Simplified distance calculation using Euclidean distance
        // For a real app, you would use the Haversine formula or Google's Distance Matrix API
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
    }

    private void updateMap() {
        if (mMap == null) return;
        
        mMap.clear();
        
        if (currentBus != null && currentBus.getCurrentLocation() != null) {
            // Add bus marker
            LatLng busLocation = new LatLng(
                    currentBus.getCurrentLocation().getLatitude(),
                    currentBus.getCurrentLocation().getLongitude());
            
            mMap.addMarker(new MarkerOptions()
                    .position(busLocation)
                    .title(currentBus.getBusNumber())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            
            // Move camera to bus location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busLocation, Constants.DEFAULT_ZOOM));
            
            // Add route stops and path if route is available
            if (currentRoute != null && currentRoute.getStops() != null && !currentRoute.getStops().isEmpty()) {
                addRouteToMap();
            }
        }
    }

    private void addRouteToMap() {
        if (mMap == null || currentRoute == null || currentRoute.getStops() == null || 
                currentRoute.getStops().isEmpty()) {
            return;
        }
        
        // Add markers for each stop
        List<LatLng> stopPoints = new ArrayList<>();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        
        for (Route.Stop stop : currentRoute.getStops()) {
            LatLng stopLocation = new LatLng(stop.getLatitude(), stop.getLongitude());
            stopPoints.add(stopLocation);
            boundsBuilder.include(stopLocation);
            
            mMap.addMarker(new MarkerOptions()
                    .position(stopLocation)
                    .title(stop.getName())
                    .snippet("Arrival: " + stop.getArrivalTime())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
        
        // Add polyline connecting all stops
        if (stopPoints.size() > 1) {
            mMap.addPolyline(new PolylineOptions()
                    .addAll(stopPoints)
                    .width(5)
                    .color(requireContext().getResources().getColor(R.color.primary)));
            
            // Include bus location in bounds
            if (currentBus != null && currentBus.getCurrentLocation() != null) {
                boundsBuilder.include(new LatLng(
                        currentBus.getCurrentLocation().getLatitude(),
                        currentBus.getCurrentLocation().getLongitude()));
            }
            
            // Zoom to show the entire route with padding
            try {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(), 100));
            } catch (Exception e) {
                // Fallback if bounds calculation fails
                if (currentBus != null && currentBus.getCurrentLocation() != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(
                                    currentBus.getCurrentLocation().getLatitude(),
                                    currentBus.getCurrentLocation().getLongitude()),
                            Constants.DEFAULT_ZOOM));
                }
            }
        }
    }

    private void showLoading(boolean isLoading) {
        mapProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Remove listeners
        if (busListener != null) {
            busListener = null;
        }
        
        if (routeListener != null) {
            routeListener = null;
        }
    }
} 
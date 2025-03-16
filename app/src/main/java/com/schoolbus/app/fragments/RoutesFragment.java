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
import com.schoolbus.app.adapters.RouteAdapter;
import com.schoolbus.app.firebase.FirebaseManager;
import com.schoolbus.app.models.Route;
import com.schoolbus.app.utils.Constants;
import com.schoolbus.app.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class RoutesFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;
    private RouteAdapter adapter;
    private List<Route> routes;
    private FirebaseManager firebaseManager;
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routes, container, false);
        
        // Initialize views
        initViews(view);
        
        // Initialize Firebase and Preferences
        firebaseManager = FirebaseManager.getInstance();
        preferenceManager = new PreferenceManager(requireContext());
        
        // Initialize routes list
        routes = new ArrayList<>();
        
        // Set up RecyclerView
        setupRecyclerView();
        
        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadRoutes);
        
        // Load routes
        loadRoutes();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.routesRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        emptyView = view.findViewById(R.id.emptyView);
    }

    private void setupRecyclerView() {
        routes = new ArrayList<>();
        adapter = new RouteAdapter(requireContext(), routes, route -> {
            // Handle route click
            if (getActivity() != null) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA_ROUTE_ID, route.getId());
                
                Fragment fragment = new RouteDetailsFragment();
                fragment.setArguments(bundle);
                
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
            }
        });
        recyclerView.setAdapter(adapter);
    }
    
    private void loadRoutes() {
        swipeRefreshLayout.setRefreshing(true);
        
        String userType = preferenceManager.getUserType();
        String userId = preferenceManager.getUserId();
        
        if (userType != null && userId != null) {
            if (userType.equals("admin")) {
                // Admin can see all routes
                firebaseManager.getAllRoutes(new FirebaseManager.DataCallback<List<Route>>() {
                    @Override
                    public void onSuccess(List<Route> data) {
                        updateRoutesList(data);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        handleError();
                    }
                });
            } else if (userType.equals("driver")) {
                // Driver sees assigned routes
                firebaseManager.getDriverRoutes(userId, new FirebaseManager.DataCallback<List<Route>>() {
                    @Override
                    public void onSuccess(List<Route> data) {
                        updateRoutesList(data);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        handleError();
                    }
                });
            } else {
                // Parent sees routes for their children's buses
                firebaseManager.getParentRoutes(userId, new FirebaseManager.DataCallback<List<Route>>() {
                    @Override
                    public void onSuccess(List<Route> data) {
                        updateRoutesList(data);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        handleError();
                    }
                });
            }
        } else {
            handleError();
        }
    }
    
    private void updateRoutesList(List<Route> data) {
        routes.clear();
        if (data != null && !data.isEmpty()) {
            routes.addAll(data);
            emptyView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
    
    private void handleError() {
        emptyView.setText(R.string.msg_no_data);
        emptyView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }
} 